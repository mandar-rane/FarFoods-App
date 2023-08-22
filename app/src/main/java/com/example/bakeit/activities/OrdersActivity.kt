package com.example.bakeit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakeit.API.ApiInterface
import com.example.bakeit.OrdersRVAdapter
import com.example.bakeit.R
import com.example.bakeit.models.AllOrdersResponse
import com.example.bakeit.models.Order
import com.example.bakeit.models.ShopResponse
import com.google.firebase.firestore.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdersActivity : AppCompatActivity() {
    private lateinit var OrderRecyclerView: RecyclerView
    private lateinit var OrderArrayList: ArrayList<Order>
    private lateinit var OrdersRVAdapter: OrdersRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        OrderArrayList = arrayListOf()

        setOrderPageRV()
        fetchAllOrders()
    }

    private fun fetchAllOrders() {
        val allOrderResp: Call<AllOrdersResponse> = ApiInterface.ApiService.ApiInstance.getAllOrders("token=${JwtManager.JwtManager.getToken(this)}")
        allOrderResp.enqueue(object: Callback<AllOrdersResponse> {
            override fun onResponse(call: Call<AllOrdersResponse>, response: Response<AllOrdersResponse>) {
                val allOrderResp: AllOrdersResponse? = response.body()
                if (allOrderResp != null){
                    Log.d("AllOrdersApiLog",allOrderResp.orders.toString())

                    allOrderResp.orders.forEach {
                        OrderArrayList.add(it)
                    }
                    Log.d("AllOrdersListLog",OrderArrayList.toString())
                    setOrderPageRV()
                }else{
                    Log.d("AllOrdersApiLog","null")
                }
            }
            override fun onFailure(call: Call<AllOrdersResponse>, t: Throwable) {
                Log.d("ExpressApiLog","api error")
            }
        })

    }




    fun setOrderPageRV() {
        OrderRecyclerView = findViewById(R.id.orders_rv)
        OrderRecyclerView.layoutManager = LinearLayoutManager(this)
        OrderRecyclerView.setHasFixedSize(true)
        OrdersRVAdapter = OrdersRVAdapter(this, OrderArrayList)
        OrderRecyclerView.adapter = OrdersRVAdapter
    }
}