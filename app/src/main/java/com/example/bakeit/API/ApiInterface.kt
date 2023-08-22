package com.example.bakeit.API

import com.bumptech.glide.Glide.init
import com.example.bakeit.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


const val BASE_URL = "https://dezz-backend.onrender.com/api/"

interface ApiInterface {
    @GET("shops/getAllShops")
    fun getAllShops(): Call<ShopResponse>

    @GET("shops/{id}")
    fun getShopById(@Path("id") shopId: String): Call<ShopDetails>


    @POST("login/sendphoneotp")
    fun sendLoginOtp(@Body phoneNum: loginUserPhone): Call<SendOtpResponse>

    @POST("login/verifyphoneotp")
    fun verifyOtp(@Body credential: verifyUserPhone): Call<loginApiResponse>

    @GET("orders/me")
    fun getAllOrders(@Header("Cookie") token: String): Call<AllOrdersResponse>

    @GET("searchshopforproduct")
    fun searchProduct(@Query("q") searchQuery: String): Call<SearchApiResponse>

    @GET("user/getuser")
    fun getUserDetails(@Header("Authorization") token: String): Call<UserDetailResponse>

    @PUT("user/updateuserdetails")
    fun updateUserProfile(@Header("Authorization") token: String, @Body user: UpdateUserRequest): Call<UpdateProfileResponse>

    @POST("user/otpphoneupdate")
    fun sendPhoneUpdateOtp(@Header("Authorization") token: String, @Body phoneNum: loginUserPhone): Call<SendOtpResponse>

    @PUT("user/verifyotpphoneupdate")
    fun verifyPhoneUpdateOtp(@Header("Authorization") token: String, @Body credential: verifyUserPhone): Call<SendOtpResponse>

    @POST("users/{userId}/address")
    fun addAddress(@Header("Authorization") token: String, @Path("userId") userId: String, @Body address:UserAddress): Call<StandardResponse>

    @POST("create-razorpay-order")
    fun createOrder(@Body requestData: RazorpayOrderRequest): Call<RazorpayOrderResponse>







    object ApiService{
        val ApiInstance: ApiInterface
        init {
            val retrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
                GsonConverterFactory.create()).build()
            ApiInstance = retrofit.create(ApiInterface::class.java)
        }
    }
}

