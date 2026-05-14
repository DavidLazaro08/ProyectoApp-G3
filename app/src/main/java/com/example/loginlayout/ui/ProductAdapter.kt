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

// Adaptador sencillo para la lista de productos
class ProductAdapter(
    private val context: Context,
    private var items: List<Product>,
    private val onAddToCart: (Product) -> Unit,
    private val onOpenDetail: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    // ViewHolder básico
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.itemImage)
        val title: TextView = view.findViewById(R.id.itemTitle)
        val price: TextView = view.findViewById(R.id.itemPrice)
        val btnAdd: Button = view.findViewById(R.id.itemAddBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return VH(v)
    }

    // Enlaza datos al item
    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.title.text = p.title
        holder.price.text = "€${String.format("%.2f", p.price)}"

        if (!p.imagePath.isNullOrEmpty()) {
            try {
                holder.img.setImageURI(Uri.parse(p.imagePath))
            } catch (e: SecurityException) {
                // permiso denegado, usar recurso de fallback
                holder.img.setImageResource(if (p.imageRes != 0) p.imageRes else R.drawable.ic_launcher_foreground)
            } catch (e: Exception) {
                holder.img.setImageResource(if (p.imageRes != 0) p.imageRes else R.drawable.ic_launcher_foreground)
            }
        } else if (p.imageRes != 0) {
            holder.img.setImageResource(p.imageRes)
        } else {
            holder.img.setImageResource(R.drawable.ic_launcher_foreground)
        }

        holder.btnAdd.setOnClickListener {
            onAddToCart(p)
        }

        holder.itemView.setOnClickListener {
            onOpenDetail(p)
        }
    }

    override fun getItemCount(): Int = items.size

    // Reemplaza lista y refresca
    fun update(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }
}
