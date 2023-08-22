package com.example.bakeit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.models.SearchApiResponse
import retrofit2.Call

class SearchTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//    fun getRatings(query: String): Call<ShopRatingResponse>{
//        val ratingResp : Call<ShopRatingResponse> = SearchInterface.WebsiteApiService.websiteApiInstance.getRatings(
//            query
//        )
//        return ratingResp
//    }




    fun searchApi(query: String): Call<SearchApiResponse> {
        val shopSearchResp: Call<SearchApiResponse> =
            ApiInterface.ApiService.ApiInstance.searchProduct(query)
//        searchResp.enqueue(object: Callback<SearchApiResponse> {
//            override fun onResponse(
//                call: Call<SearchApiResponse>,
//                response: Response<SearchApiResponse>
//            ) {
//                val searchResp: SearchApiResponse? = response.body()
//                if (searchResp != null){
//                    Log.d("TypesenseLog",searchResp.hits.toString())
//                }else{
//                    Log.d("TypesenseLog","null")
//                }
//            }
//
//            override fun onFailure(call: Call<SearchApiResponse>, t: Throwable) {
//                Log.d("TypesenseLog","Error in search")
//            }
//        })
        return shopSearchResp
    }

}