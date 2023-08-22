package com.example.bakeit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakeit.R
import com.example.bakeit.SearchResultAdapter
import com.example.bakeit.databinding.ActivitySearchResultBinding
import com.example.bakeit.models.SearchApiResponse
import com.example.bakeit.models.SearchApiResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultActivity : AppCompatActivity() {
    private lateinit var query: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var shopResultArrayList: ArrayList<SearchApiResult>
    private lateinit var SearchResultPageAdapter: SearchResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shopResultArrayList = arrayListOf()

        query = intent.getStringExtra("query").toString()
        binding.resultHeader.text = "All bakeries delivering ${query}"

        binding.svSearchResultActivity.setQuery(query,false)

        binding.svSearchResultActivity.setOnClickListener{
            startActivity(Intent(this,SearchActivity::class.java))
        }


        fetchResults(query)

        setSearchResultRV()
    }

    private fun fetchResults(query: String?) {
        val SearchApiResponse = SearchTestActivity().searchApi(query.toString())
        decodeApiResponse(SearchApiResponse)

    }

    private fun decodeApiResponse(searchResp: Call<SearchApiResponse>) {

        searchResp.enqueue(object: Callback<SearchApiResponse> {
            override fun onResponse(
                call: Call<SearchApiResponse>,
                response: Response<SearchApiResponse>
            ) {
                val searchResp: SearchApiResponse? = response.body()
                if (searchResp != null){
                    //Log.d("searchResultLog",searchResp.hits.toString())
                    shopResultArrayList.clear()
                    searchResp.result.forEach { shopResultArrayList.add(it) }
                    setSearchResultRV()
                    Log.d("searchResultLogi","${shopResultArrayList.toString()}")

                }else{
                    shopResultArrayList.clear()
                    setSearchResultRV()
                    Log.d("ShopSearchApiLog","null")
                }
            }

            override fun onFailure(call: Call<SearchApiResponse>, t: Throwable) {
                Log.d("ShopSearchApiLog","Error in search")
            }
        })
    }

    private fun setSearchResultRV() {
        recyclerView = findViewById(R.id.search_result_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        Log.d("searchqueryyy", "$query")
        SearchResultPageAdapter = SearchResultAdapter(this, shopResultArrayList, query)
        recyclerView.adapter = SearchResultPageAdapter
    }
}