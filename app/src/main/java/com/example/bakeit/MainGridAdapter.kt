package com.example.bakeit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakeit.activities.ProductCategoryResultActivity
import com.example.bakeit.models.MainGridItem

class MainGridAdapter(private var context: Context, val gridItemList: ArrayList<MainGridItem>):
    RecyclerView.Adapter<MainGridAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val gridItemName: TextView = itemView.findViewById(R.id.grid_item_name)
        val gridItemImage: ImageView = itemView.findViewById(R.id.grid_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.main_page_grid_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gridItem: MainGridItem = gridItemList[position]
        holder.gridItemName.text = gridItem.catName


        Glide
            .with(context)
            .load(gridItem.catImage)
            .centerCrop()
            .placeholder(R.drawable.food_placeholder_image)
            .into(holder.gridItemImage)

        holder.gridItemImage.setOnClickListener {
            sendInfoToCategoryResult(holder.gridItemName.text.toString())
        }
    }

    private fun sendInfoToCategoryResult(name: String) {
        val intent: Intent = Intent(context, ProductCategoryResultActivity::class.java)
        intent.putExtra("NAME", name)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return gridItemList.size
    }

}