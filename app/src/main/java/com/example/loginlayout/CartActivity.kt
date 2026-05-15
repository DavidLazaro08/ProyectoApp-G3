package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private var isAdmin = false
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""

        val txtCartItems = findViewById<TextView>(R.id.txtCartItems)
        val txtCartPrice = findViewById<TextView>(R.id.txtCartPrice)
        val txtCartQuantity = findViewById<TextView>(R.id.txtCartQuantity)
        val txtSummaryItems = findViewById<TextView>(R.id.txtSummaryItems)
        val txtSummarySubtotal = findViewById<TextView>(R.id.txtSummarySubtotal)
        val txtSummaryTax = findViewById<TextView>(R.id.txtSummaryTax)
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val btnBackCatalog = findViewById<Button>(R.id.btnBackCatalog)
        val imgCartProduct = findViewById<ImageView>(R.id.imgCartProduct)

        val db = DBHelper(this)
        val cart = db.getCartItems()

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

            val first = cart[0]
            val product = db.getProductById(first.first)
            if (product != null) {
                txtCartItems.text = product.title
                txtCartPrice.text = if (product.price == 0.0) "GRATIS"
                    else String.format("€%.2f", product.price)
                txtCartQuantity.text = "Cantidad: "
                if (!product.imagePath.isNullOrEmpty()) {
                    try {
                        imgCartProduct.setImageURI(android.net.Uri.parse(product.imagePath))
                    } catch (e: Exception) {
                        if (product.imageRes != 0) imgCartProduct.setImageResource(product.imageRes)
                    }
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
            txtSummaryTax.text = String.format("Tax (21%%): €%.2f", tax)
            txtTotal.text = String.format("TOTAL: €%.2f", total)
        }

        btnBackCatalog.setOnClickListener { finish() }

        val btnCheckout = findViewById<Button>(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            db.purchaseCart()
            android.widget.Toast.makeText(this, "¡Compra realizada! Los juegos están en tu biblioteca.", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        }

        setupBottomNav()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: username
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_cart
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_store -> {
                    startActivity(Intent(this, EcommerceActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin)
                        putExtra("username", username)
                    })
                    true
                }
                R.id.nav_library -> {
                    startActivity(Intent(this, CatalogActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin)
                        putExtra("username", username)
                    })
                    true
                }
                R.id.nav_cart -> true
                else -> false
            }
        }
    }
}
