package com.example.bakeit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakeit.models.SubCategoryItem

class ProductSubCategoryRVAdapter(private var context: Context, val rvItemList: ArrayList<SubCategoryItem>, private val subCatSelectListener: (name: String) -> Unit): RecyclerView.Adapter<ProductSubCategoryRVAdapter.ViewHolder>()  {
    private var selectedItemPosition = 0
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)  {
        val subCategoryItemName: TextView = itemView.findViewById(R.id.subcat_item_name)
        val subCategoryItemImage: ImageView = itemView.findViewById(R.id.subcat_item_image)
        val selectedIndicator: View = itemView.findViewById(R.id.subcat_selected_underline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.subcategory_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subCatItem: SubCategoryItem = rvItemList[position]
        holder.subCategoryItemName.text = subCatItem.subCatName
        //holder.subCategoryItemImage.setImageResource(gridItem.image)
        holder.itemView.setOnClickListener {
            subCatSelectListener.invoke(holder.subCategoryItemName.text.toString())
            selectedItemPosition = holder.adapterPosition
            notifyDataSetChanged()
        }

        if (selectedItemPosition == position) {
            holder.selectedIndicator.visibility = View.VISIBLE
        } else {
            holder.selectedIndicator.visibility = View.INVISIBLE
        }
        Glide
            .with(context)
            .load(subCatItem.subCatImage)
            .centerCrop()
            .placeholder(R.drawable.food_placeholder_image)
            .into(holder.subCategoryItemImage)
    }

    override fun getItemCount(): Int {
        return rvItemList.size
    }
}

