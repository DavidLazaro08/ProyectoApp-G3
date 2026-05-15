package com.example.loginlayout.ui

import android.content.Context
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginlayout.R
import com.example.loginlayout.data.Product

class OfferAdapter(
    private val context: Context,
    private var items: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<OfferAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.offerImg)
        val title: TextView = view.findViewById(R.id.offerTitle)
        val discount: TextView = view.findViewById(R.id.offerDiscount)
        val priceNew: TextView = view.findViewById(R.id.offerPriceNew)
        val priceOld: TextView = view.findViewById(R.id.offerPriceOld)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.offer_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.title.text = p.title
        holder.discount.text = "-${p.discount}%"
        val discounted = p.price * (1 - p.discount / 100.0)
        holder.priceNew.text = "€%.2f".format(discounted)
        holder.priceOld.text = "€%.2f".format(p.price)
        holder.priceOld.paintFlags = holder.priceOld.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        when {
            !p.imagePath.isNullOrEmpty() -> {
                try { holder.img.setImageURI(Uri.parse(p.imagePath)) }
                catch (e: Exception) { setFallbackImage(holder.img, p) }
            }
            p.imageRes != 0 -> holder.img.setImageResource(p.imageRes)
        }

        holder.itemView.setOnClickListener { onClick(p) }
    }

    private fun setFallbackImage(img: ImageView, p: Product) {
        if (p.imageRes != 0) img.setImageResource(p.imageRes)
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }
}
