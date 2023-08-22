package com.example.bakeit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakeit.models.Products

class ShopProductsAdapter(
    private var context: Context,
    val productList: ArrayList<Products>,
    private val deleteListener: (id: String, price: Double) -> Unit,
    private val addListener: (id: String, name: String, price: Double, img: String, shop: String) -> Unit,
    val shopId: String,
    private val openProdDescriptionBottomSheet: (name: String, image: String, description: String) -> Unit,

    ) :
    RecyclerView.Adapter<ShopProductsAdapter.ViewHolder>()


{


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        val productImage: ImageView = itemView.findViewById(R.id.iv_product_image)
        val addBtn: TextView = itemView.findViewById(R.id.add_item_btn)
        val removeBtn: TextView = itemView.findViewById(R.id.remove_item_btn)
        val itemQty: TextView = itemView.findViewById(R.id.item_qty_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_products_list_item, parent, false)

        return ShopProductsAdapter.ViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product: Products = productList[position]
        holder.productName.text = product.prodname.toString()
        holder.productPrice.text = "\u20B9${product.prodprice}"
        holder.addBtn.setOnClickListener {
            addListener.invoke(product._id.toString()  ,product.prodname.toString(), product.prodprice!!, product.prodimages!![0] , shopId)
        }
        holder.removeBtn.setOnClickListener {
            deleteListener(product._id.toString(), product.prodprice!!)
        }
        holder.itemView.setOnClickListener {
            openProdDescriptionBottomSheet(product.prodname!!, product.prodimages!![0],
                product.proddescription.toString()
            )
        }



        Glide
            .with(context)
            .load(product.prodimages!![0])
            .centerCrop()
            .placeholder(R.drawable.food_placeholder_image)
            .into(holder.productImage)


        //sendInfoToProductDescription(holder.productName.text.toString(), product.productPrice, product.productImage)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

/*private fun sendInfoToProductDescription(name: String, price: Int?, img: String?) {

    val intent: Intent = Intent(context, ProductDescriptionActivity::class.java)
    intent.putExtra("NAME",name)
    intent.putExtra("PRICE",price)
    intent.putExtra("IMG",img)
    context.startActivity(intent)
}*/
