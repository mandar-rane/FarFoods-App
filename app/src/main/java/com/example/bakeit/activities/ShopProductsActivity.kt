package com.example.bakeit.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.R
import com.example.bakeit.ShopProductsAdapter
import com.example.bakeit.databinding.ActivityShopProductsBinding
import com.example.bakeit.models.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.jar.Attributes

class ShopProductsActivity : RoomBaseActivity() {
    private lateinit var shopInstance: Shops
    private lateinit var productArrayList: ArrayList<Products>
    private lateinit var ShopProductsAdapter: ShopProductsAdapter
    private lateinit var binding: ActivityShopProductsBinding
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopInstance = intent.getParcelableExtra("shop")!!
        Log.d("ShopActivityStart", "Shop from main activity: ${shopInstance.toString()}")

        val shopName = shopInstance.shopname
        val shopOutlet = shopInstance.shopoutlet
        binding.shopNameProductsList.text = shopName
        binding.outletAddress.text = shopOutlet
        binding.shoprating.text = shopInstance.shopavgrating.toString()
        if (shopInstance.veg){
            binding.shopTagsVegImg.visibility = View.VISIBLE
            binding.shopTagsVeg.visibility = View.VISIBLE
            binding.shopTagsSeparator1.visibility = View.VISIBLE
        }
        binding.shopTag1.text = shopInstance.shopcategories[0]
        binding.shopTag2.text = shopInstance.shopcategories[1]
        productArrayList = arrayListOf()

        setShopProductsRV()
        fetchShopContents(shopInstance._id)


        val checkoutIntent = Intent(this, CheckoutActivity::class.java)

        checkoutIntent.putExtra("shop", shopInstance)

        binding.cartPreview.setOnClickListener {
            startActivity(checkoutIntent)
        }

        //getRatingsFromApi()
    }

//    private fun getRatingsFromApi() {
//        val shopRatingResponse = SearchTestActivity().getRatings("63a04bed4421406a2fb2fc6f")
//        decodeApiResponse(shopRatingResponse)
//    }
//
//    private fun decodeApiResponse(shopRatingResponse: Call<ShopRatingResponse>) {
//        shopRatingResponse.enqueue(object: Callback<ShopRatingResponse> {
//            override fun onResponse(
//                call: Call<ShopRatingResponse>,
//                response: Response<ShopRatingResponse>
//            ) {
//                val searchResp: ShopRatingResponse? = response.body()
//                if (searchResp != null){
//                    binding.shoprating.text = searchResp.shop.averagerating.toString()
//                    binding.shopRatingCard.visibility = View.INVISIBLE
//                }else{
//                    binding.shopRatingCard.visibility = View.INVISIBLE
//                    Log.d("BFULog","null")
//                }
//            }
//
//            override fun onFailure(call: Call<ShopRatingResponse>, t: Throwable) {
//                Log.d("BFULog","Error in search")
//                binding.shopRatingCard.visibility = View.VISIBLE
//            }
//
//        })
//    }


    private fun fetchShopContents(shopID: String) {
        val shopResp: Call<ShopDetails> = ApiInterface.ApiService.ApiInstance.getShopById(shopID)
        Log.d("shopprod", shopResp .toString())
        shopResp.enqueue(object : Callback<ShopDetails> {
            override fun onResponse(call: Call<ShopDetails>, response: Response<ShopDetails>) {
                val shopResp: ShopDetails? = response.body()
                Log.d("ProductsApiLog", shopResp.toString())
                if (shopResp != null) {
                    shopResp.products.forEach {
                        productArrayList.add(it)
                    }
                    Log.d("ShopArrayList", productArrayList.toString())
                    setShopProductsRV()
                } else {
                    Log.d("ProductsApiLog", "null")
                }
            }

            override fun onFailure(call: Call<ShopDetails>, t: Throwable) {
                Log.d("shopprods", "${t}")
            }
        })

    }

    private fun addItem(pid: String, name: String, price: Double, img: String, shopId: String) {
        CoroutineScope(IO).launch {
            val existingCartItems = getCartItemsForShop()
            if (existingCartItems.isEmpty()) {
                val existingItem = getCartItem(pid)
                if (existingItem == null) {
                    addItemToCart(CartEntity(pid, name, price, img, shopId, price, 1))
                } else {
                    existingItem.quantity += 1
                    existingItem.totalItemPrice += price
                    update(existingItem)
                }

            } else {
                if (existingCartItems[0].itemShopId == shopId) {
                    val existingItem = getCartItem(pid)
                    if (existingItem == null) {
                        addItemToCart(CartEntity(pid, name, price, img, shopId, price, 1))
                    } else {
                        existingItem.quantity += 1
                        existingItem.totalItemPrice += price
                        update(existingItem)
                    }
                } else {
                    clearCart()
                    val existingItem = getCartItem(pid)
                    if (existingItem == null) {
                        addItemToCart(CartEntity(pid, name, price, img, shopId, price, 1))
                    } else {
                        existingItem.quantity += 1
                        existingItem.totalItemPrice += price
                        update(existingItem)
                    }
                }
            }
            getCartPreviewStatistics()
        }
    }

    private fun setShopProductsRV() {
        recyclerView = findViewById(R.id.rv_main_page)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        ShopProductsAdapter = ShopProductsAdapter(
            this,
            productArrayList,
            { deleteId, price -> deleteRecord(deleteId, price) },
            { addRecPid, addRecName, addRecPrice, addRecImg, addRecShop ->
                addItem(
                    addRecPid,
                    addRecName,
                    addRecPrice,
                    addRecImg,
                    addRecShop
                )
            },
            shopInstance._id,
            {prodName, prodImage, prodDescription -> openProdDescriptionBottomSheet(prodName, prodImage, prodDescription)}
        )
        recyclerView.adapter = ShopProductsAdapter
    }

    private fun openProdDescriptionBottomSheet(
        prodName: String,
        prodImage: String,
        prodDescription: String
    ) {
        val prodDescriptionBottomSheet = BottomSheetDialog(this)
        prodDescriptionBottomSheet.setContentView(R.layout.prod_desc_bottom_sheet)

        val prodNameBottomSheet = prodDescriptionBottomSheet.findViewById<TextView>(R.id.prodname_tv)
        val prodImageBottomSheet = prodDescriptionBottomSheet.findViewById<ImageView>(R.id.prod_iv)
        val prodDescBottomSheet = prodDescriptionBottomSheet.findViewById<TextView>(R.id.prodDesc_tv)

        Glide
            .with(this)
            .load(prodImage)
            .centerCrop()
            .placeholder(R.drawable.ic_baseline_location_off_24)
            .into(prodImageBottomSheet!!)

        prodNameBottomSheet?.text = prodName
        prodDescBottomSheet?.text = prodDescription

        prodDescriptionBottomSheet.show()

    }

    private fun deleteRecord(id: String, price: Double) {
        CoroutineScope(IO).launch {
            val existingItem = getCartItem(id)
            if (existingItem.quantity > 1) {
                existingItem.quantity -= 1
                existingItem.totalItemPrice -= price
                update(existingItem)
            } else {
                deleteCartItem(CartEntity(id))
            }

            getCartPreviewStatistics()
        }

    }

    private fun getCartPreviewStatistics() {
        CoroutineScope(IO).launch {
            val cartItemCount = getCartQty()
            val cartPrice = getCartTotalPrice()
            val existingCartItems = getCartItemsForShop()
            if (existingCartItems.isEmpty()) {
                Log.d("prevd", "getCartPreviewStatistics func if called")
                updatePreviewCard(cartItemCount, cartPrice, "")
            } else {
                updatePreviewCard(cartItemCount, cartPrice, existingCartItems[0].itemShopId)
                Log.d("prevd", "getCartPreviewStatistics func else called")
            }


        }
    }

    private suspend fun updatePreviewCard(cartItemCount: Int, cartPrice: Double, shopId: String) {
        Log.d("prevd", "cartItemCount${cartItemCount} &&  ${shopId} == ${shopInstance._id}")
        Log.d("prevd", "updatePreviewCard func called")
        withContext(Main) {
            if (cartItemCount != 0 && shopId == shopInstance._id) {
                Log.d("prevd", "made VISIBLE")
                binding.cartPreview.visibility = View.VISIBLE
                binding.cartPreviewQty.text = cartItemCount.toString()
                binding.cartPreviewPrice.text = getString(R.string.price, cartPrice)
            } else {
                binding.cartPreview.visibility = View.INVISIBLE
                Log.d("prevd", "made IVISIBLE")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCartPreviewStatistics()
    }
}