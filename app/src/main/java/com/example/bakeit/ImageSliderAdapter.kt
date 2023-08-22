package com.example.bakeit

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
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
import com.example.bakeit.models.Products
import com.example.bakeit.models.Shops

class ImageSliderAdapter(
    private var context: Context,
    private val queryResultProducts: ArrayList<Products>,
    private val imageUrlList: ArrayList<String>,
    private val viewPager2: ViewPager2,
    private val shop: Shops
): RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {
    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.slider_container_iv)
        val name_preview: TextView = itemView.findViewById(R.id.name_preview_tv)
        val price_preview: TextView = itemView.findViewById(R.id.price_preview_tv)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageSliderAdapter.ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_image_container, parent,false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageSliderAdapter.ImageViewHolder, position: Int) {
        val imgUrl = queryResultProducts[position].prodimages?.get(0)
        holder.name_preview.text = queryResultProducts[position].prodname
        holder.price_preview.text = "\u20B9${queryResultProducts[position].prodprice}"
        holder.imageView.setOnClickListener {
            sendInfoToProductDescription(shop)
        }


        Glide
            .with(holder.itemView)
            .load(imgUrl)
            .centerCrop()
            .placeholder(R.drawable.food_placeholder_image)
            .into(holder.imageView)

        if(position == queryResultProducts.size-1){
            viewPager2.post(runnable_one)
        }

        setViewPager()
    }

    private fun setViewPager() {
        val handler = Handler(Looper.myLooper()!!)

        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit= 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 4000)

            }
        })

    }

    private fun sendInfoToProductDescription(shop: Shops) {
        val intent = Intent(context, ShopProductsActivity::class.java)
        intent.putExtra("shop",shop)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return queryResultProducts.size
    }

    private val runnable_one = Runnable {
        queryResultProducts.addAll(queryResultProducts)
        notifyDataSetChanged()
    }


    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }
}