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
import com.bumptech.glide.Glide
import com.example.bakeit.activities.ShopProductsActivity
import com.example.bakeit.models.Shops

class MainPageAdapter(private var context: Context, val shopList: ArrayList<Shops>):RecyclerView.Adapter<MainPageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
//        fun bind(shop: Shops) {
//            val imageUrls = shop.shopimg
//            val imageSliderAdapter = ImageSliderAdapter(imageUrls!!,shopImage)
//            shopImage.adapter = imageSliderAdapter
//        }

        val shopName : TextView = itemView.findViewById(R.id.tv_shop_name)
        val shopOutlet : TextView = itemView.findViewById(R.id.tv_shop_outlet)
        //val shopImageVP : ViewPager2 = itemView.findViewById(R.id.vp_shop_image)
        val shopImageIV : ImageView = itemView.findViewById(R.id.iv_shop_image)
        val shopRating: TextView = itemView.findViewById(R.id.shop_rating)
        val shopVegIV: ImageView = itemView.findViewById(R.id.shop_tags_veg_img)
        val shopVeg: TextView = itemView.findViewById(R.id.shop_tags_veg)
        val shopTag1: TextView = itemView.findViewById(R.id.shop_tag_1)
        val shopTag2: TextView = itemView.findViewById(R.id.shop_tag_2)
        val tagSeparator1: TextView = itemView.findViewById(R.id.shop_tags_separator_1)
        val tagSeparator2: TextView = itemView.findViewById(R.id.shop_tags_separator_2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_main, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shop: Shops = shopList[position]
        //holder.bind(shop)
        holder.shopName.text = shop.shopname
        holder.shopOutlet.text = shop.shopoutlet
        holder.shopRating.text = shop.shopavgrating.toString()
        holder.itemView.setOnClickListener{
            sendInfoToProductDescription(shop)
        }

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

        Glide
            .with(context)
            .load(shop.shopimg)
            .centerCrop()
            .placeholder(R.drawable.food_placeholder_image)
            .into(holder.shopImageIV)
    }

    override fun getItemCount(): Int {
        return  shopList.size
    }

    private fun sendInfoToProductDescription(shop: Shops) {
        val intent = Intent(context, ShopProductsActivity::class.java)
        intent.putExtra("shop",shop)
        context.startActivity(intent)
    }
}