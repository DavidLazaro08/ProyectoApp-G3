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

        val nombreJuego = intent.getStringExtra("nombreJuego") ?: "Aún no se ha añadido ningún producto"
        val precioJuego = intent.getDoubleExtra("precioJuego", 0.0)

        val tax = precioJuego * 0.21
        val total = precioJuego + tax

        txtCartItems.text = nombreJuego
        txtCartPrice.text = String.format("€%.2f", precioJuego)
        txtCartQuantity.text = "Cantidad: 1"
        txtSummaryItems.text = "Items: 1"
        txtSummarySubtotal.text = String.format("Subtotal: €%.2f", precioJuego)
        txtSummaryTax.text = String.format("Tax: €%.2f", tax)
        txtTotal.text = String.format("TOTAL: €%.2f", total)

        when (nombreJuego) {
            "NEON STREETS REDUX" -> imgCartProduct.setImageResource(R.drawable.neon_streets)
            "SHADOW BLADE DX" -> imgCartProduct.setImageResource(R.drawable.shadow_blade)
            "RETRO STORM" -> imgCartProduct.setImageResource(R.drawable.retro_storm)
        }

        btnBackCatalog.setOnClickListener {
            finish()
        }
    }
}