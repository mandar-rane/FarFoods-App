package com.example.bakeit

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.bakeit.activities.ShopProductsActivity
import com.example.bakeit.models.Products
import com.example.bakeit.models.SearchApiResult
import com.example.bakeit.models.Shops

class ProductSubCategoryResultRVAdapter(
    private var context: Context,
    val resultList: ArrayList<SearchApiResult>,
    val productName: String
) : RecyclerView.Adapter<ProductSubCategoryResultRVAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(queryResultProducts: ArrayList<Products>, shop: Shops, context: Context) {

            val imageUrlList = ArrayList<String>()

            for (product in queryResultProducts){
                for(productImage in product.prodimages ?: emptyList()){
                    imageUrlList.add(productImage)
                }
            }

            val imageSliderAdapter = ImageSliderAdapter(context, queryResultProducts,imageUrlList, shopImage, shop)
            shopImage.adapter = imageSliderAdapter

        }

        val shopName: TextView = itemView.findViewById(R.id.tv_shop_name_result)
        val shopOutlet: TextView = itemView.findViewById(R.id.tv_shop_outlet_result)
        val shopImage : ViewPager2 = itemView.findViewById(R.id.vp_shop_image_result)
        val shopVegIV: ImageView = itemView.findViewById(R.id.shop_tags_veg_img_result)
        val shopVeg: TextView = itemView.findViewById(R.id.shop_tags_veg_result)
        val shopTag1: TextView = itemView.findViewById(R.id.shop_tag_1_result)
        val shopTag2: TextView = itemView.findViewById(R.id.shop_tag_2_result)
        val tagSeparator1: TextView = itemView.findViewById(R.id.shop_tags_separator_1_result)
        val tagSeparator2: TextView = itemView.findViewById(R.id.shop_tags_separator_2_result)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_result, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shop: Shops = resultList[position].shop
        holder.shopName.text = shop.shopname
        holder.shopOutlet.text = shop.shopoutlet

        holder.itemView.setOnClickListener {
            sendInfoToProductDescription(shop)
        }

        Log.d("ResAdap", shop.shopcategories.toString())
        holder.shopTag1.text = shop.shopcategories[0]
        holder.shopTag2.text = shop.shopcategories[1]

        if(shop.veg){
            holder.shopVegIV.visibility = View.VISIBLE
            holder.shopVeg.visibility = View.VISIBLE
            holder.tagSeparator1.visibility = View.VISIBLE
        }else{
            holder.shopVegIV.visibility = View.GONE
            holder.tagSeparator1.visibility = View.GONE
        }



        holder.bind(resultList[position].products as ArrayList<Products>, shop, context)







//        Glide
//            .with(context)
//            .load(shop.shopimg!![0])
//            .centerCrop()
//            .placeholder(R.drawable.ic_baseline_location_off_24)
//            .into(holder.shopImage)


        /*holder.shopImage.setOnClickListener{
            sendInfoToProductDescription(holder.shopName.text.toString(), holder.shopOutlet.text.toString(), shop.shopimg)
        }*/
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    private fun sendInfoToProductDescription(shop: Shops) {
        Log.d("tchdd", "touchedd${shop.shopname}")
        val intent = Intent(context, ShopProductsActivity::class.java)
        intent.putExtra("shop",shop)
        context.startActivity(intent)
    }
}