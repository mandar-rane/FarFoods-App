package com.example.bakeit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bakeit.models.AllOrdersResponse
import com.example.bakeit.models.Order


class OrdersRVAdapter(private var context: Context, val ordersList: ArrayList<Order>) : RecyclerView.Adapter<OrdersRVAdapter.ViewHolder>(){
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val shopname : TextView = itemView.findViewById(R.id.orders_shop_name_tv)
        val shopAddr : TextView = itemView.findViewById(R.id.orders_shop_outlet_tv)
        val status: TextView = itemView.findViewById(R.id.order_status_indicator)
        val linearLay: LinearLayout = itemView.findViewById(R.id.linear_layout_orders)
        val total: TextView = itemView.findViewById(R.id.order_total)
        val time: TextView = itemView.findViewById(R.id.order_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_orders, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order: Order = ordersList[position]
        holder.shopname.text = "Shop Name"
        holder.shopAddr.text = "Shop Address"
        holder.status.text = order.orderstatus
        holder.total.text = "₹${order.totalprice.toString()}"
        holder.time.text = order.paidat.toString()
        holder.linearLay.removeAllViews()



        for(i in order.orderitems){
            val item = LayoutInflater.from(context).inflate(R.layout.order_contents_lv_item,null, false)
            val cont = item.findViewById<TextView>(R.id.content_name)
            val qty = item.findViewById<TextView>(R.id.content_qty)
            cont.text = i.name
            qty.text = "${i.quantity}x"
            //val itemsTV = TextView(context)
            //itemsTV.text = i
            holder.linearLay.addView(item)
        }


        /*if (holder.status.text == "Processing"){
            // TODO:
        }else if(holder.status.text == "Confirmed"){
            // TODO:
        }else{
            // TODO:  
        }*/

    }

    override fun getItemCount(): Int {
        return ordersList.size
    }
}