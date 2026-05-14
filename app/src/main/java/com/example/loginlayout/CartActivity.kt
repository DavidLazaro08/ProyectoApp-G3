package com.example.loginlayout

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val txtCartItems = findViewById<TextView>(R.id.txtCartItems)
        val txtCartPrice = findViewById<TextView>(R.id.txtCartPrice)
        val txtCartQuantity = findViewById<TextView>(R.id.txtCartQuantity)
        val txtSummaryItems = findViewById<TextView>(R.id.txtSummaryItems)
        val txtSummarySubtotal = findViewById<TextView>(R.id.txtSummarySubtotal)
        val txtSummaryTax = findViewById<TextView>(R.id.txtSummaryTax)
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val btnBackCatalog = findViewById<Button>(R.id.btnBackCatalog)
        val imgCartProduct = findViewById<ImageView>(R.id.imgCartProduct)

        val db = com.example.loginlayout.data.DBHelper(this)
        val cart = db.getCartItems()

        // Muestra resumen simple del carrito persistente
        if (cart.isEmpty()) {
            txtCartItems.text = "Carrito vacío"
            txtCartPrice.text = "€0.00"
            txtCartQuantity.text = "Cantidad: 0"
            txtSummaryItems.text = "Items: 0"
            txtSummarySubtotal.text = "Subtotal: €0.00"
            txtSummaryTax.text = "Tax: €0.00"
            txtTotal.text = "TOTAL: €0.00"
        } else {
            var subtotal = 0.0
            var itemsCount = 0
            // Mostrar primer producto como ejemplo
            val first = cart[0]
            val product = db.getProductById(first.first)
            if (product != null) {
                txtCartItems.text = product.title
                txtCartPrice.text = String.format("€%.2f", product.price)
                txtCartQuantity.text = "Cantidad: ${first.second}"
                if (!product.imagePath.isNullOrEmpty()) {
                    imgCartProduct.setImageURI(android.net.Uri.parse(product.imagePath))
                } else if (product.imageRes != 0) {
                    imgCartProduct.setImageResource(product.imageRes)
                }
            }

            for (entry in cart) {
                val prod = db.getProductById(entry.first)
                if (prod != null) {
                    subtotal += prod.price * entry.second
                    itemsCount += entry.second
                }
            }

            val tax = subtotal * 0.21
            val total = subtotal + tax

            txtSummaryItems.text = "Items: $itemsCount"
            txtSummarySubtotal.text = String.format("Subtotal: €%.2f", subtotal)
            txtSummaryTax.text = String.format("Tax: €%.2f", tax)
            txtTotal.text = String.format("TOTAL: €%.2f", total)
        }

        btnBackCatalog.setOnClickListener {
            finish()
        }
        // Simula el checkout: vacía el carrito y muestra confirmación
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            db.clearCart()
            android.widget.Toast.makeText(this, "Compra simulada. Carrito vaciado.", android.widget.Toast.LENGTH_SHORT).show()
            // Volvemos a la pantalla principal
            finish()
        }
    }
}