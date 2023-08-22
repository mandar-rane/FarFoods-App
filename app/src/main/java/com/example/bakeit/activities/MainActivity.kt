package com.example.bakeit.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.LocationServices.LocationService

import com.example.bakeit.MainActivityCardSliderAdapter
import com.example.bakeit.MainPageAdapter
import com.example.bakeit.R
import com.example.bakeit.databinding.ActivityMainBinding
import com.example.bakeit.models.*
import com.example.bakeit.MainGridAdapter
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), LocationService.LocationCallback {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gridView: RecyclerView
    private lateinit var gridList: ArrayList<MainGridItem>
    private lateinit var gridAdapter: MainGridAdapter
    private lateinit var shopArrayList: ArrayList<Shops>
    private lateinit var MainPageAdapter: MainPageAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var cardVpImageList: ArrayList<Int>
    private lateinit var sliderAdapter: MainActivityCardSliderAdapter
    private lateinit var handler: Handler
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchUserDetails()

        binding.shimmerMainPageRv.startShimmer()

        binding.mainActivityAccountSymbol.setOnClickListener {
            val profileIntent = Intent(this, UserProfileActivity::class.java)
            startActivity(profileIntent)
        }

        binding.svMainPage.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        shopArrayList = arrayListOf()

        setViewPager()



        setMainPageGV()
        setMainPageRV()


        EventChangeListener()
        fetchShopsFromApi()

        setAddressPreview()

    }

    private fun setAddressPreview() {
        val detailedAddress = LocationService().locationFetcher(this, this)
    }

    private fun fetchUserDetails() {
        val userResp: Call<UserDetailResponse> =
            ApiInterface.ApiService.ApiInstance.getUserDetails(JwtManager.JwtManager.getToken(this).toString())
        Log.d("uuresp", userResp.toString())
        userResp.enqueue(object : Callback<UserDetailResponse> {
            override fun onResponse(
                call: Call<UserDetailResponse>,
                response: Response<UserDetailResponse>
            ) {

                val userResp: UserDetailResponse? = response.body()
                Log.d("uuresp", userResp.toString())
                if (userResp != null && userResp.success == "true") {
                    val fetchedUser = User(userResp.user._id.toString(),
                        userResp.user.email.toString(),
                        userResp.user.username.toString(),
                        userResp.user.phone.toString(),
                        userResp.user.addresses)

                    val userPrefs = UserPreferences(this@MainActivity)
                    userPrefs.setUser(fetchedUser)

//                    saveUserDetails(fetchedUser)
//                    saveUserDetails( userResp.user._id.toString(),
//                        userResp.user.email.toString(),
//                        userResp.user.username.toString(),
//                        userResp.user.phone.toString(),
//                        userResp.user.address.toString())


                } else {
                    Log.d("ExpressApiLog", "null")
                }
            }

            override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                Log.d("ExpressApiLog", "api error")
            }
        })

    }

//    private fun saveUserDetails(
//        user: User
//    ) {
//        val gson = Gson()
//        val userJson = gson.toJson(user)
//        Log.d("userrapi", "spcalled")
//        val userDetailSharedPreferences =
//            getSharedPreferences("userDetailPrefs", Context.MODE_PRIVATE)
//        val editor = userDetailSharedPreferences.edit()
//        editor.putString("userInSharedPref", userJson)
//        editor.apply()
//    }

    private fun fetchShopsFromApi() {
        val shopResp: Call<ShopResponse> = ApiInterface.ApiService.ApiInstance.getAllShops()
        shopResp.enqueue(object : Callback<ShopResponse> {
            override fun onResponse(call: Call<ShopResponse>, response: Response<ShopResponse>) {
                val shopResp: ShopResponse? = response.body()
                if (shopResp != null) {
                    binding.shimmerMainPageRv.stopShimmer()
                    binding.shimmerMainPageRv.visibility = View.GONE
                    binding.rvMainPage.visibility = View.VISIBLE
                    shopResp.shops.forEach {
                        shopArrayList.add(it)
                    }
                    Log.d("MainActivityShopApiCall", shopArrayList.toString())
                    setMainPageRV()
                } else {
                    Log.d("MainActivityShopApiCall", "null")
                }
            }

            override fun onFailure(call: Call<ShopResponse>, t: Throwable) {
                Log.d("MainActivityShopApiCall", "api error: ${t}")
            }
        })
    }

    private fun setMainPageGV() {
        gridView = binding.mainGrid
        gridView.setHasFixedSize(true)
        gridView.layoutManager = GridLayoutManager(this, 4)
        gridList = arrayListOf()
        gridAdapter = MainGridAdapter(this, gridList)
        gridView.adapter = gridAdapter
    }

    fun setMainPageRV() {
        recyclerView = binding.rvMainPage
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        MainPageAdapter = MainPageAdapter(this, shopArrayList)
        recyclerView.adapter = MainPageAdapter
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()

        db.collection("ProdCategory").addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        gridList.add(dc.document.toObject(MainGridItem::class.java))
                    }
                }
                gridAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setViewPager() {
        viewPager2 = findViewById<ViewPager2>(R.id.main_activity_card_vp)
        handler = Handler(Looper.myLooper()!!)
        cardVpImageList = ArrayList() //
        cardVpImageList.add(R.drawable.discounts_vp)
        cardVpImageList.add(R.drawable.burg_vp)

        sliderAdapter = MainActivityCardSliderAdapter(cardVpImageList, viewPager2)
        viewPager2.adapter = sliderAdapter
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 4000)
            }
        })
    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 200)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    override fun onLocationReceived(location: Location) {
        val location = location
        LocationService.GeocoderUtil.getAddress(this,location.latitude,location.longitude){
                address: Address? ->

            runOnUiThread {
                if (address != null) {
                    val locality = address.locality ?: ""
                    val thoroughfare = address.thoroughfare ?: ""
                    val subAdmin = address.subAdminArea ?: ""
                    val subLocality = address.subLocality ?: ""

                    val formattedAddress = "$thoroughfare, $locality"
                    binding.addressPreviewMainActivity.text = subLocality
                    binding.detailedAddressPreviewMainActivity.text = address.getAddressLine(0)

                } else {
                    Log.d("geocheck", "null")
                }
            }
        }
    }

    override fun onLocationError(errorMessage: String) {
        Log.d("locerr", errorMessage)
    }


//    fun updateAddress(user: User) {
//        val address_preview = user.useraddr
//        if (address_preview == " ") {
//            showLocationReqPopup()
//        } else {
//            binding.addressPreviewMainActivity.text = user.useraddr
//        }
//    }

//    @SuppressLint("MissingInflatedId")
//    fun showLocationReqPopup() {
//        val dialogBinding = layoutInflater.inflate(R.layout.loc_popup, null)
//        val locRequest: Dialog = Dialog(this)
//        locRequest.setContentView(R.layout.loc_popup)
//        locRequest.setCancelable(false)
//        locRequest.show()
//        val popup_et = locRequest.findViewById<TextView>(R.id.loc_popup_main_et)
//        val save_btn = locRequest.findViewById<Button>(R.id.save_btn_loc_pop)
//    }
//
//    fun userRegisteredSuccess() {
//        Toast.makeText(this, "Succly redg", Toast.LENGTH_LONG).show()
//    }


}
