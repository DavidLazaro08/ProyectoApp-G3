package com.example.loginlayout

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
 * Pantalla del carrito.
 * Muestra el estado actual de la compra y permite continuar al proceso de checkout.
 */
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

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""

        db = DBHelper(this)

        layoutCartEmpty = findViewById(R.id.layoutCartEmpty)
        layoutCartProduct = findViewById(R.id.layoutCartProduct)
        imgCartProduct = findViewById(R.id.imgCartProduct)
        txtCartItems = findViewById(R.id.txtCartItems)
        txtCartPrice = findViewById(R.id.txtCartPrice)
        txtCartQuantity = findViewById(R.id.txtCartQuantity)
        txtSummaryItems = findViewById(R.id.txtSummaryItems)
        txtSummarySubtotal = findViewById(R.id.txtSummarySubtotal)
        txtSummaryTax = findViewById(R.id.txtSummaryTax)
        txtTotal = findViewById(R.id.txtTotal)
        btnCheckout = findViewById(R.id.btnCheckout)

        findViewById<Button>(R.id.btnBackCatalog).setOnClickListener {
            finish()
        }

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

        findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId = R.id.nav_cart
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: username
    }

    private fun refreshCart() {
        val cart = db.getCartItems()

        if (cart.isEmpty()) {
            showEmptyCart()
        } else {
            showCartContent(cart)
        }
    }

    private fun showEmptyCart() {
        layoutCartEmpty.visibility = View.VISIBLE
        layoutCartProduct.visibility = View.GONE

        txtSummaryItems.text = "Items: 0"
        txtSummarySubtotal.text = "Subtotal: €0.00"
        txtSummaryTax.text = "Tax: €0.00"
        txtTotal.text = "TOTAL: €0.00"

        btnCheckout.isEnabled = false
        btnCheckout.alpha = 0.4f
        btnCheckout.text = "CARRITO VACÍO"
    }

    private fun showCartContent(cart: List<Pair<Int, Int>>) {
        layoutCartEmpty.visibility = View.GONE
        layoutCartProduct.visibility = View.VISIBLE

        btnCheckout.isEnabled = true
        btnCheckout.alpha = 1f
        btnCheckout.text = "REALIZAR COMPRA"

        // En esta versión se muestra el primer producto como resumen visual del carrito.
        val firstItem = cart[0]
        val product = db.getProductById(firstItem.first)

        if (product != null) {
            txtCartItems.text = product.title
            txtCartPrice.text = if (product.price == 0.0) {
                "GRATIS"
            } else {
                "€%.2f".format(product.price)
            }
            txtCartQuantity.text = "Cantidad: ${firstItem.second}"

            loadProductImage(product)
        }

        updateSummary(cart)
    }

    private fun loadProductImage(product: com.example.loginlayout.data.Product) {
        when {
            !product.imagePath.isNullOrEmpty() -> {
                try {
                    imgCartProduct.setImageURI(Uri.parse(product.imagePath))
                } catch (e: Exception) {
                    if (product.imageRes != 0) imgCartProduct.setImageResource(product.imageRes)
                }
            }

            product.imageRes != 0 -> {
                imgCartProduct.setImageResource(product.imageRes)
            }
        }
    }

    private fun updateSummary(cart: List<Pair<Int, Int>>) {
        var subtotal = 0.0
        var itemsCount = 0

        for (entry in cart) {
            val product = db.getProductById(entry.first)

            if (product != null) {
                subtotal += product.price * entry.second
                itemsCount += entry.second
            }
        }

        val tax = subtotal * 0.21
        val total = subtotal + tax

        txtSummaryItems.text = "Items: $itemsCount"
        txtSummarySubtotal.text = "Subtotal: €%.2f".format(subtotal)
        txtSummaryTax.text = "Tax (21%%): €%.2f".format(tax)
        txtTotal.text = "TOTAL: €%.2f".format(total)
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.nav_cart

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    openActivity(HomeActivity::class.java)
                    true
                }

                R.id.nav_store -> {
                    openActivity(EcommerceActivity::class.java)
                    true
                }

                R.id.nav_library -> {
                    openActivity(CatalogActivity::class.java)
                    true
                }

                R.id.nav_cart -> true

                R.id.nav_profile -> {
                    openActivity(ProfileActivity::class.java)
                    true
                }

                else -> false
            }
        }
    }

    private fun openActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass).apply {
            putExtra("isAdmin", isAdmin)
            putExtra("username", username)
        })
    }
}