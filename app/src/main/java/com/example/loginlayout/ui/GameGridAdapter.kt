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

/*
 * Adapter para mostrar los juegos en formato cuadrícula.
 * Se usa principalmente en la biblioteca del usuario.
 */
class GameGridAdapter(
    private val context: Context,
    private var items: List<Product>,
    private val onOpenDetail: (Product) -> Unit
) : RecyclerView.Adapter<GameGridAdapter.GameGridViewHolder>() {

    inner class GameGridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.gridItemImage)
        val title: TextView = view.findViewById(R.id.gridItemTitle)
        val category: TextView = view.findViewById(R.id.gridItemCategory)
        val price: TextView = view.findViewById(R.id.gridItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameGridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_grid_item, parent, false)

        return GameGridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameGridViewHolder, position: Int) {
        val product = items[position]

        holder.title.text = product.title
        holder.category.text = product.category
        holder.price.text = if (product.price == 0.0) {
            "GRATIS"
        } else {
            "€%.2f".format(product.price)
        }

        // Cargamos imagen externa si existe. Si no, usamos el recurso local del producto.
        if (!product.imagePath.isNullOrEmpty()) {
            try {
                holder.img.setImageURI(Uri.parse(product.imagePath))
            } catch (e: Exception) {
                holder.img.setImageResource(
                    if (product.imageRes != 0) product.imageRes else R.drawable.ic_launcher_foreground
                )
            }
        } else if (product.imageRes != 0) {
            holder.img.setImageResource(product.imageRes)
        } else {
            holder.img.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.itemView.setOnClickListener {
            onOpenDetail(product)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }
}