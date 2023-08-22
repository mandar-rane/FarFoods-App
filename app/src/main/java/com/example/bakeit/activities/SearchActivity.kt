package com.example.bakeit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakeit.R
import com.example.bakeit.SearchPageAdapter
import com.example.bakeit.databinding.ActivitySearchBinding
import com.example.bakeit.models.Products
import com.example.bakeit.models.SearchApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private lateinit var SearchMatchArrayList: ArrayList<Products>
    private lateinit var SearchPageAdapter: SearchPageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.shimmerSv.stopShimmer()
        binding.shimmerSv.visibility = View.GONE
        SearchMatchArrayList = arrayListOf()

        setSearchPageRV()








        /*val user = arrayOf("abhay","bob")

        val userAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, user
        )

        findViewById<ListView>(R.id.search_result_lv).adapter = userAdapter

        findViewById<androidx.appcompat.widget.SearchView>(R.id.sv_search_activity).setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (user.contains(query)){
                    userAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter.filter(newText)
                return false
            }

        })*/

        binding.svSearchActivity.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                binding.searchMatchRv.visibility = View.GONE
                binding.shimmerSv.visibility = View.VISIBLE
                binding.shimmerSv.startShimmer()
                Log.d("qwert",newText)
                if (newText.isNotBlank()){
                    Log.d("qwert",newText)
                    val searchApiResponse = SearchTestActivity().searchApi(newText)
                    decodeApiResponse(searchApiResponse)
                }else if (newText.isBlank()){
                    Log.d("qwertuu",newText)
                    binding.shimmerSv.stopShimmer()
                    binding.shimmerSv.visibility = View.GONE
                    SearchMatchArrayList.clear()
                    setSearchPageRV()
                }
                return false
            }

        })
    }

    private fun decodeApiResponse(searchResp: Call<SearchApiResponse>) {
        searchResp.enqueue(object: Callback<SearchApiResponse> {
            override fun onResponse(
                call: Call<SearchApiResponse>,
                response: Response<SearchApiResponse>
            ) {
                val searchResp: SearchApiResponse? = response.body()
                    Log.d("TypesenseLog","${searchResp}")
                if (searchResp != null){
                    binding.shimmerSv.stopShimmer()
                    binding.shimmerSv.visibility = View.GONE
                    binding.searchMatchRv.visibility = View.VISIBLE
                    SearchMatchArrayList.clear()
                    for (shop in searchResp.result){
                        SearchMatchArrayList.addAll(shop.products)
                    }
                    setSearchPageRV()
                    //Log.d("TypesenseLog",SearchMatchArrayList.toString())
                }else{
                    binding.shimmerSv.stopShimmer()
                    binding.shimmerSv.visibility = View.GONE
                    binding.searchMatchRv.visibility = View.VISIBLE
                    Toast.makeText(this@SearchActivity,"not found",Toast.LENGTH_SHORT).show()
                    Log.d("TypesenseLog","null")
                }
            }

            override fun onFailure(call: Call<SearchApiResponse>, t: Throwable) {
                Log.d("TypesenseLog","Error in search")
            }
        })
    }

    private fun setSearchPageRV() {
        recyclerView = findViewById(R.id.search_match_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
//        SearchMatchArrayList = arrayListOf()
        SearchPageAdapter = SearchPageAdapter(this,SearchMatchArrayList)
        recyclerView.adapter = SearchPageAdapter
    }
}