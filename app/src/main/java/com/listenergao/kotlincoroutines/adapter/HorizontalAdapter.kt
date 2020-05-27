package com.listenergao.kotlincoroutines.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.listenergao.kotlincoroutines.R
import com.listenergao.kotlincoroutines.model.ItemData

class HorizontalAdapter(private val items: List<ItemData>) :
    RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val content = itemView.findViewById<TextView>(R.id.tv_content)

        fun onBindData(data: ItemData) {
            content.text = data.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindData(items[position])
    }
}