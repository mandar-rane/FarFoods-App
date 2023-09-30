package com.example.bakeit.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.CheckoutPageAdapter
import com.example.bakeit.R
import com.example.bakeit.databinding.ActivityCheckoutBinding
import com.example.bakeit.models.RazorpayOrderRequest
import com.example.bakeit.models.RazorpayOrderResponse
import com.example.bakeit.models.Shops
import com.example.bakeit.models.User
import com.example.bakeit.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.play.integrity.internal.t
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
//import kotlinx.coroutines.flow.EmptyFlow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CheckoutActivity : RoomBaseActivity(), PaymentResultWithDataListener {
    private lateinit var modeOfPayment: String
    private lateinit var checkoutUser: User
    private lateinit var shopName: String
    private lateinit var shopOutlet: String
    private lateinit var shopInstance: Shops
    private lateinit var recyclerView: RecyclerView
    private lateinit var CheckoutPageAdapter: CheckoutPageAdapter
    private lateinit var binding: ActivityCheckoutBinding
    private var cartTotal: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(IO).launch {
            cartTotal = getCartTotalPrice()
        }

        Toast.makeText(this,"${cartTotal}", Toast.LENGTH_SHORT).show()

        getUser()
        fillCartRV()
        getCartStatistics()


        shopInstance = intent.getParcelableExtra("shop")!!
        shopName = shopInstance.shopname
        shopOutlet = shopInstance.shopoutlet

        modeOfPayment = Constants.DEFAULT_PAYMENT_MODE
        setPaymentMode()

        binding.placeOrderBtn.setOnClickListener {
            if (modeOfPayment == Constants.OTHER_PAYMENT_MODE) {
                startRazorpayFlow()
            } else if (modeOfPayment == Constants.CASH_ON_DELIVERY_PAYMENT_MODE) {
                startCodFlow()
            }
        }

        binding.paymentOptionOpener.setOnClickListener {
            //startActivity(Intent(this, PaymentOptionActivity::class.java))
            val paymentBottomSheet = BottomSheetDialog(this)
            paymentBottomSheet.setContentView(R.layout.payment_option_bottomsheet_layout)
            paymentBottomSheet.show()
            paymentBottomSheet.findViewById<LinearLayout>(R.id.otherpay_option)!!.setOnClickListener {
                modeOfPayment = Constants.OTHER_PAYMENT_MODE
                setPaymentMode()
                paymentBottomSheet.dismiss()
            }
            paymentBottomSheet.findViewById<LinearLayout>(R.id.cod_option)!!.setOnClickListener {
                modeOfPayment = Constants.CASH_ON_DELIVERY_PAYMENT_MODE
                setPaymentMode()
                paymentBottomSheet.dismiss()
            }
        }
    }


    private fun startCodFlow() {

        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(R.layout.place_order_bottom_sheet_layout)
        bottomSheet.show()

        val progressBar = bottomSheet.findViewById<LinearProgressIndicator>(R.id.progress_bar)
        var progress = 0
        val maxProgress = 100
        val increment = 1

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (progress < maxProgress) {
                        progress += increment
                        progressBar!!.progress = progress
                    } else {
                        timer.cancel()

                        CoroutineScope(IO).launch {
                            placeOrder(getCartItemNames(), getCartTotalPrice())
                        }
                    }
                }
            }
        }, 0, 50)


    }

    private fun startRazorpayFlow() {

        val requestData = RazorpayOrderRequest(
            (cartTotal*100).toInt(),
           "INR"
        )

        val call = ApiInterface.ApiService.ApiInstance.createOrder(requestData)
        call.enqueue(object : Callback<RazorpayOrderResponse> {
            override fun onResponse(call: Call<RazorpayOrderResponse>, response: Response<RazorpayOrderResponse>) {
                if (response.isSuccessful) {
                    val order = response.body()
                    startRazorpayPayment(order!!.id)
                } else {
                    Log.e("razorpayCreateOrder", "createOrderError")
                }
            }

            override fun onFailure(call: Call<RazorpayOrderResponse>, t: Throwable) {
                Log.e("razorpayCreateOrder", "createOrderFailed: ${t}")
            }
        })
    }

    private fun startRazorpayPayment(orderId: String) {

        val co = Checkout()
        co.setKeyID("")

        try {
            val options = JSONObject()
            options.put("name", "Dezerto")
            options.put("description","Food Order")
            //You can omit the image option to fetch the image from the dashboard
//            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
            options.put("order_id", orderId);

            options.put("amount",cartTotal*100)

            val prefill = JSONObject()
            prefill.put("email",checkoutUser.email)
            prefill.put("contact",checkoutUser.phone)

            options.put("prefill",prefill)

            co.open(this, options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun getUser() {
        val userDetailSharedPreferences =
            getSharedPreferences("userDetailPrefs", Context.MODE_PRIVATE)
        checkoutUser = User(
            userDetailSharedPreferences.getString("name","").toString(),
            userDetailSharedPreferences.getString("name","").toString(),
            userDetailSharedPreferences.getString("name","").toString(),
            userDetailSharedPreferences.getString("name","").toString()
        )
    }

    private fun fillCartRV() {
        CoroutineScope(IO).launch {
            getStackedCartItems().collect {
                val list = ArrayList(it)
                setCheckoutPageRV(list)
            }
        }
    }

    fun setCheckoutPageRV(cartProductArrayList: ArrayList<CartEntity>) {
        this@CheckoutActivity.runOnUiThread(java.lang.Runnable {
            recyclerView = binding.rvCheckoutPage
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)
            CheckoutPageAdapter = CheckoutPageAdapter(
                this,
                cartProductArrayList,
                { deleteId, price -> deleteRecord(deleteId, price) },
                { id, name, price, img -> addItem(id, name, price, img) })
            recyclerView.adapter = CheckoutPageAdapter
            getCartStatistics()
        })
    }

    private fun getCartStatistics() {
        CoroutineScope(IO).launch {
            val cartItemCount = getCartQty()
            val cartTotal = getCartTotalPrice()
            setCheckout(cartTotal, cartItemCount)
        }
    }

    private suspend fun setCheckout(cartTotal: Double, cartItemCount: Int) {
        val deliver_charge = 0.0
        val grand_total = cartTotal + deliver_charge
        withContext(Main) {
            binding.billSummaryItemTotalVal.text = getString(R.string.price, cartTotal)
            binding.totalPlaceOrderBtn.text = getString(R.string.price, grand_total)
            binding.deliveryChargeValue.text = getString(R.string.price, deliver_charge)
            binding.grandTotalValue.text = getString(R.string.price, grand_total)
            if (cartItemCount == 0) {
                finish()
            }
        }
    }

    private suspend fun setCheckoutBtn(checkoutUser: User) {
        withContext(Main) {
            if (checkoutUser.username == "" || checkoutUser.phone == "" || checkoutUser.addresses[0].completeAddress == "") {
                binding.DeliveryAddressLabelCheckout.visibility = GONE
                binding.addressCheckoutSummary.visibility = GONE
                binding.lineDivider.visibility = GONE
                binding.placeOrderBtnCardLay.visibility = GONE
                binding.enterPersonalDetailsBtn.visibility = VISIBLE
                binding.yourDetails.visibility = GONE
            } else {
                binding.DeliveryAddressLabelCheckout.visibility = VISIBLE
                binding.addressCheckoutSummary.visibility = VISIBLE
                binding.lineDivider.visibility = VISIBLE
                binding.placeOrderBtnCardLay.visibility = VISIBLE
                binding.enterPersonalDetailsBtn.visibility = GONE
                binding.yourDetails.visibility = VISIBLE
                binding.addressCheckoutSummary.text = checkoutUser.addresses[0].completeAddress
                binding.yourDetailsName.text = "${checkoutUser.username}, ${checkoutUser.phone}"
            }
        }

        val toDetailIntent = Intent(this, PersonalDetails::class.java)
        toDetailIntent.putExtra("checkoutUser", checkoutUser)
        binding.enterPersonalDetailsBtn.setOnClickListener {
            startActivity(toDetailIntent)
        }
    }

    private fun addItem(id: String, name: String, price: Double, img: String) {
        CoroutineScope(IO).launch {
            val existingItem = getCartItem(id)
            if (existingItem == null) {
                addItemToCart(CartEntity(id, name, price, img, "", price, 1))
            } else {
                existingItem.quantity += 1
                existingItem.totalItemPrice += price
                update(existingItem)
            }
        }
    }


    private fun placeOrder(cartItems: List<String>, cartTotalPrice: Double) {
        val orderTimestamp = Calendar.getInstance().time
        val currUser = checkoutUser
//        FirebaseFirestore.getInstance().collection(Constants.USERS).document(currUser.uid)
//            .collection("Orders").document().set(
//                Order(
//                    "", cartItems,
//                    cartTotalPrice, "Processing", "", shopName, "", shopOutlet, currUser.uid,
//                    currUser.username, currUser.userphone, currUser.useraddr, orderTimestamp
//                ),
//                SetOptions.merge()
//            ).addOnSuccessListener {
//                clearCartAfterOrder()
//                orderplaced()
//                Log.d("Order", "Order Placed")
//            }
        orderplaced()
    }

    private fun clearCartAfterOrder() {
        CoroutineScope(IO).launch {
            clearCart()
        }
    }

    private fun orderplaced() {
        startActivity(Intent(this, OrderPlacedActivity::class.java))
    }

    fun deleteRecord(id: String, price: Double) {
        CoroutineScope(IO).launch {
            val existingItem = getCartItem(id)
            if (existingItem.quantity > 1) {
                existingItem.quantity -= 1
                existingItem.totalItemPrice -= price
                update(existingItem)
            } else {
                deleteCartItem(CartEntity(id))
            }

        }
    }


    fun setPaymentMode() {
//        val paymentPref = getSharedPreferences("paymentModePrefs", Context.MODE_PRIVATE)
//        modeOfPayment = paymentPref.getString("payment_mode", "").toString()
        if (modeOfPayment == Constants.OTHER_PAYMENT_MODE) {
            binding.modeOfPaymentTv.text = "Other Payment Mode"
            binding.modeOfPaymentIcon.setImageResource(R.drawable.google_pay_icon_small)
        } else if (modeOfPayment == Constants.CASH_ON_DELIVERY_PAYMENT_MODE) {
            binding.modeOfPaymentTv.text = "Cash On Delivery"
            binding.modeOfPaymentIcon.setImageResource(R.drawable.cash_icon_small)
        }
    }


    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(this, "Payment Successful ${p0}", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        val paymentFailedBottomSheet = BottomSheetDialog(this)
        paymentFailedBottomSheet.setContentView(R.layout.payment_failed_bottom_sheet)
        paymentFailedBottomSheet.show()
        Toast.makeText(this, "Payment Error ${p0}", Toast.LENGTH_SHORT).show()
    }

}
