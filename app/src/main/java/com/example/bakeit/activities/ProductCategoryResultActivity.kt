package com.example.bakeit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakeit.ProductSubCategoryResultRVAdapter
//import com.example.bakeit.ProductSubCategoryResultRVAdapter
import com.example.bakeit.databinding.ActivityProductCategoryResultBinding
import com.example.bakeit.models.*
import com.example.bakeit.ProductSubCategoryRVAdapter
import com.google.firebase.firestore.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductCategoryResultActivity : AppCompatActivity() {
    private lateinit var subCatResultRV: RecyclerView
    private lateinit var categoryQuery: String
    private lateinit var subCategoryQuery: String
    private lateinit var searchQuery: String

    private lateinit var CategoryName: String

    private lateinit var subCategoryRV: RecyclerView
    private lateinit var subCategoryRVAdapter: ProductSubCategoryRVAdapter
    private lateinit var subCategoryList: ArrayList<SubCategoryItem>
    private lateinit var subCategoryResultRV: RecyclerView
    private lateinit var subCategoryResultRVAdapter: ProductSubCategoryResultRVAdapter
    private lateinit var subCategoryResultList: ArrayList<SearchApiResult>
   private lateinit var binding: ActivityProductCategoryResultBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCategoryResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subCategoryResultList = arrayListOf()

        CategoryName = intent.getStringExtra("NAME")!!

        categoryQuery = CategoryName
        subCategoryQuery = ""

        searchQuery = subCategoryQuery + categoryQuery
        subCatSelected(subCategoryQuery)

        binding.menuName.text = "All bakeries delivering ${CategoryName}"

        Toast.makeText(this,"${searchQuery}",Toast.LENGTH_SHORT).show()
        //binding.productCategoryName.text = CategoryName.toString()
        setUpProdSubCategoryRV()
        setUpProdSubCategoryResultRV()
        EventChangeListener(CategoryName)
    }

    private fun setUpProdSubCategoryResultRV() {
        subCategoryResultRV = binding.subCategoryResultRv
        subCategoryResultRV.setHasFixedSize(true)
        subCategoryResultRV.layoutManager = LinearLayoutManager(this)
        //addSubCategoryItemsToList()

        subCategoryResultRVAdapter = ProductSubCategoryResultRVAdapter(this, subCategoryResultList, searchQuery)
        subCategoryResultRV.adapter = subCategoryResultRVAdapter
    }

    private fun EventChangeListener(CategoryName: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("ProdCategory").document(CategoryName).collection("ProdSubCategory").addSnapshotListener(object : EventListener<QuerySnapshot> {
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
                        subCategoryList.add(dc.document.toObject(SubCategoryItem::class.java))
                    }
                }
                subCategoryRVAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun subCatSelected(subCatName: String){
        if(subCatName!="All" && subCatName != ""){

            searchQuery = "$subCatName $categoryQuery"
            Log.d("subcatselect","$searchQuery")
            fetchResults(searchQuery)
        }else{
            searchQuery = categoryQuery + ""
            fetchResults(searchQuery)
        }
    }

    private fun fetchResults(query: String?) {
        val shopSearchApiResponse = SearchTestActivity().searchApi(query!!)
        decodeApiResponse(shopSearchApiResponse)

    }

    private fun decodeApiResponse(searchResp: Call<SearchApiResponse>) {
        searchResp.enqueue(object: Callback<SearchApiResponse> {
            override fun onResponse(
                call: Call<SearchApiResponse>,
                response: Response<SearchApiResponse>
            ) {
                val searchResp: SearchApiResponse? = response.body()
                if (searchResp != null){

                    subCategoryResultList.clear()
                    searchResp.result.forEach { subCategoryResultList.add(it) }
                    setUpProdSubCategoryResultRV()
                    Log.d("searchResultLog",subCategoryResultList.toString())
                }else{
                    subCategoryResultList.clear()
                    setUpProdSubCategoryResultRV()
                    Log.d("ShopSearchApiLog","null")
                }
            }
            override fun onFailure(call: Call<SearchApiResponse>, t: Throwable) {
                Log.d("ShopSearchApiLog","Error in search")
            }
        })
    }

    private fun setUpProdSubCategoryRV() {
        subCategoryRV = binding.productSubCategoryRv
        subCategoryRV.setHasFixedSize(true)
        subCategoryRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        subCategoryList = arrayListOf()
        //addSubCategoryItemsToList()
        subCategoryRVAdapter = ProductSubCategoryRVAdapter(this, subCategoryList){
            name ->  subCatSelected(name)
        }
        subCategoryRV.adapter = subCategoryRVAdapter

    }

    /*private fun addSubCategoryItemsToList() {
        subCategoryList.add(SubCategoryItem(R.drawable.oreo_cake2, "Cakes"))
        subCategoryList.add(SubCategoryItem(R.drawable.pastry, "Pastries"))
        subCategoryList.add(SubCategoryItem(R.drawable.cappucin, "Cappuccino"))
        subCategoryList.add(SubCategoryItem(R.drawable.shakes, "Shakes"))
        subCategoryList.add(SubCategoryItem(R.drawable.donuts, "Donuts"))
        subCategoryList.add(SubCategoryItem(R.drawable.waffles, "Waffles"))
        subCategoryList.add(SubCategoryItem(R.drawable.brownie, "Brownies"))
        subCategoryList.add(SubCategoryItem(R.drawable.croissants, "Croissants"))
    }*/
}