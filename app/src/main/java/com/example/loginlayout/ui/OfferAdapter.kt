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

/*
 * Adapter para la sección de ofertas.
 * Muestra productos con descuento en una lista horizontal.
 */
class OfferAdapter(
    private val context: Context,
    private var items: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {

    inner class OfferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.offerImg)
        val title: TextView = view.findViewById(R.id.offerTitle)
        val discount: TextView = view.findViewById(R.id.offerDiscount)
        val priceNew: TextView = view.findViewById(R.id.offerPriceNew)
        val priceOld: TextView = view.findViewById(R.id.offerPriceOld)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.offer_item, parent, false)

        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val product = items[position]

        holder.title.text = product.title
        holder.discount.text = "-${product.discount}%"

        val discountedPrice = product.price * (1 - product.discount / 100.0)

        holder.priceNew.text = "€%.2f".format(discountedPrice)
        holder.priceOld.text = "€%.2f".format(product.price)
        holder.priceOld.paintFlags = holder.priceOld.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        // Primero intentamos cargar imagen externa. Si falla, usamos la imagen local.
        when {
            !product.imagePath.isNullOrEmpty() -> {
                try {
                    holder.img.setImageURI(Uri.parse(product.imagePath))
                } catch (e: Exception) {
                    setFallbackImage(holder.img, product)
                }
            }

            product.imageRes != 0 -> {
                holder.img.setImageResource(product.imageRes)
            }
        }

        holder.itemView.setOnClickListener {
            onClick(product)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun setFallbackImage(img: ImageView, product: Product) {
        if (product.imageRes != 0) {
            img.setImageResource(product.imageRes)
        }
    }
}