package com.example.loginlayout.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginlayout.R
import com.example.loginlayout.data.Product

/*
 * Adapter del listado principal de productos.
 * Se encarga de pintar cada juego en el RecyclerView y gestionar sus acciones básicas.
 */
class ProductAdapter(
    private val context: Context,
    private var items: List<Product>,
    private val onAddToCart: (Product) -> Unit,
    private val onOpenDetail: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.itemImage)
        val title: TextView = view.findViewById(R.id.itemTitle)
        val price: TextView = view.findViewById(R.id.itemPrice)
        val btnAdd: Button = view.findViewById(R.id.itemAddBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = items[position]

        holder.title.text = product.title
        holder.price.text = if (product.price == 0.0) {
            "GRATIS"
        } else {
            "€%.2f".format(product.price)
        }

        // Si el producto tiene imagen externa, se intenta cargar primero.
        // Si no existe o falla, se usa la imagen local del recurso.
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

        holder.btnAdd.setOnClickListener {
            onAddToCart(product)
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