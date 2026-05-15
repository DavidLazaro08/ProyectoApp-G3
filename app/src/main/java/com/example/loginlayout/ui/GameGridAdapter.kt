package com.example.loginlayout.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginlayout.R
import com.example.loginlayout.data.Product

class GameGridAdapter(
    private val context: Context,
    private var items: List<Product>,
    private val onOpenDetail: (Product) -> Unit
) : RecyclerView.Adapter<GameGridAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.gridItemImage)
        val title: TextView = view.findViewById(R.id.gridItemTitle)
        val category: TextView = view.findViewById(R.id.gridItemCategory)
        val price: TextView = view.findViewById(R.id.gridItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.game_grid_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.title.text = p.title
        holder.category.text = p.category
        holder.price.text = if (p.price == 0.0) "GRATIS" else "€${String.format("%.2f", p.price)}"

        if (!p.imagePath.isNullOrEmpty()) {
            try {
                holder.img.setImageURI(Uri.parse(p.imagePath))
            } catch (e: Exception) {
                holder.img.setImageResource(if (p.imageRes != 0) p.imageRes else R.drawable.ic_launcher_foreground)
            }
        } else if (p.imageRes != 0) {
            holder.img.setImageResource(p.imageRes)
        } else {
            holder.img.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.itemView.setOnClickListener { onOpenDetail(p) }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }
}
