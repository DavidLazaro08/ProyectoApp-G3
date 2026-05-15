package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private var isAdmin = false
    private var username = ""
    private lateinit var db: DBHelper

    private lateinit var layoutCartEmpty: View
    private lateinit var layoutCartProduct: View
    private lateinit var imgCartProduct: ImageView
    private lateinit var txtCartItems: TextView
    private lateinit var txtCartPrice: TextView
    private lateinit var txtCartQuantity: TextView
    private lateinit var txtSummaryItems: TextView
    private lateinit var txtSummarySubtotal: TextView
    private lateinit var txtSummaryTax: TextView
    private lateinit var txtTotal: TextView
    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        isAdmin  = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""
        db       = DBHelper(this)

        layoutCartEmpty   = findViewById(R.id.layoutCartEmpty)
        layoutCartProduct = findViewById(R.id.layoutCartProduct)
        imgCartProduct    = findViewById(R.id.imgCartProduct)
        txtCartItems      = findViewById(R.id.txtCartItems)
        txtCartPrice      = findViewById(R.id.txtCartPrice)
        txtCartQuantity   = findViewById(R.id.txtCartQuantity)
        txtSummaryItems   = findViewById(R.id.txtSummaryItems)
        txtSummarySubtotal= findViewById(R.id.txtSummarySubtotal)
        txtSummaryTax     = findViewById(R.id.txtSummaryTax)
        txtTotal          = findViewById(R.id.txtTotal)
        btnCheckout       = findViewById(R.id.btnCheckout)

        findViewById<Button>(R.id.btnBackCatalog).setOnClickListener { finish() }

        btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java).apply {
                putExtra("isAdmin", isAdmin)
                putExtra("username", username)
            })
        }

        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        refreshCart()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        isAdmin  = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: username
    }

    private fun refreshCart() {
        val cart = db.getCartItems()

        if (cart.isEmpty()) {
            layoutCartEmpty.visibility   = View.VISIBLE
            layoutCartProduct.visibility = View.GONE
            txtSummaryItems.text    = "Items: 0"
            txtSummarySubtotal.text = "Subtotal: €0.00"
            txtSummaryTax.text      = "Tax: €0.00"
            txtTotal.text           = "TOTAL: €0.00"
            btnCheckout.isEnabled   = false
            btnCheckout.alpha       = 0.4f
            btnCheckout.text        = "CARRITO VACÍO"
        } else {
            layoutCartEmpty.visibility   = View.GONE
            layoutCartProduct.visibility = View.VISIBLE
            btnCheckout.isEnabled = true
            btnCheckout.alpha     = 1f
            btnCheckout.text      = "REALIZAR COMPRA"

            val first   = cart[0]
            val product = db.getProductById(first.first)
            if (product != null) {
                txtCartItems.text    = product.title
                txtCartPrice.text    = if (product.price == 0.0) "GRATIS" else "€%.2f".format(product.price)
                txtCartQuantity.text = "Cantidad: ${first.second}"
                when {
                    !product.imagePath.isNullOrEmpty() -> try {
                        imgCartProduct.setImageURI(android.net.Uri.parse(product.imagePath))
                    } catch (e: Exception) {
                        if (product.imageRes != 0) imgCartProduct.setImageResource(product.imageRes)
                    }
                    product.imageRes != 0 -> imgCartProduct.setImageResource(product.imageRes)
                }
            }

            var subtotal   = 0.0
            var itemsCount = 0
            for (entry in cart) {
                val prod = db.getProductById(entry.first)
                if (prod != null) {
                    subtotal   += prod.price * entry.second
                    itemsCount += entry.second
                }
            }

            val tax   = subtotal * 0.21
            val total = subtotal + tax
            txtSummaryItems.text    = "Items: $itemsCount"
            txtSummarySubtotal.text = "Subtotal: €%.2f".format(subtotal)
            txtSummaryTax.text      = "Tax (21%%): €%.2f".format(tax)
            txtTotal.text           = "TOTAL: €%.2f".format(total)
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_cart
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    }); true
                }
                R.id.nav_store -> {
                    startActivity(Intent(this, EcommerceActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    }); true
                }
                R.id.nav_library -> {
                    startActivity(Intent(this, CatalogActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    }); true
                }
                R.id.nav_cart -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    }); true
                }
                else -> false
            }
        }
    }
}
