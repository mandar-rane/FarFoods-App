package com.example.bakeit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakeit.activities.CartEntity

class CheckoutPageAdapter(
    private var context: Context,
    val cartProductList: ArrayList<CartEntity>,
    private val deleteListener: (id: String, price: Double) -> Unit,
    private val addListener: (pid: String, itemName: String, itemPrice: Double, itemImg: String ) -> Unit
) :
    RecyclerView.Adapter<CheckoutPageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartProductName: TextView = itemView.findViewById(R.id.tv_cart_product_name)
        val cartProductPrice: TextView = itemView.findViewById(R.id.tv_cart_product_price)
        val cartProductImage: ImageView = itemView.findViewById(R.id.iv_cart_product_image)
        val cartProductDelete: TextView = itemView.findViewById(R.id.item_remove_btn_minus)
        val cartProductQuantity: TextView = itemView.findViewById(R.id.item_qty_in_btn)
        val cartProductAdd: TextView = itemView.findViewById(R.id.item_remove_btn_plus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_checkout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product: CartEntity = cartProductList[position]
        holder.cartProductQuantity.text = product.quantity.toString()
        holder.cartProductName.text = product.itemName
        holder.cartProductPrice.text = "₹${product.totalItemPrice}"
        holder.cartProductDelete.setOnClickListener {
            deleteListener.invoke(product.pid, product.itemPrice)
        }
        holder.cartProductAdd.setOnClickListener {
            addListener.invoke(product.pid.toString(), product.itemName, product.itemPrice, product.itemImg)
        }

        Glide
            .with(context)
            .load(product.itemImg)
            .centerCrop()
            .placeholder(R.drawable.food_placeholder_image)
            .into(holder.cartProductImage)
    }

    override fun getItemCount(): Int {
        return cartProductList.size
    }
}