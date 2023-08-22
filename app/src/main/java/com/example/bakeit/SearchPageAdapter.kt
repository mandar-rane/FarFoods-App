package com.example.bakeit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakeit.activities.SearchResultActivity
import com.example.bakeit.models.Products

class SearchPageAdapter(private var context: Context, val matchList: ArrayList<Products>): RecyclerView.Adapter<SearchPageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val Name : TextView = itemView.findViewById(R.id.result_product_name)
        val Image: ImageView = itemView.findViewById(R.id.result_product_image)
        val Type: TextView = itemView.findViewById(R.id.result_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return SearchPageAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return  matchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            Toast.makeText(context,"${holder.Name.text} pressed",Toast.LENGTH_SHORT).show()
            sendQueryToSearchResultActivity(holder.Name.text.toString())
        }
        val match: Products = matchList[position]
        holder.Name.text = match.prodname
        holder.Type.text = ""

        Glide
            .with(context)
            .load(match.prodimages!![0])
            .centerCrop()
            .placeholder(R.drawable.food_placeholder_image)
            .into(holder.Image)
    }

    private fun sendQueryToSearchResultActivity(query: String) {
        val toResultActivityIntent: Intent = Intent(context, SearchResultActivity::class.java)
        toResultActivityIntent.putExtra("query",query)
        context.startActivity(toResultActivityIntent)
    }
}