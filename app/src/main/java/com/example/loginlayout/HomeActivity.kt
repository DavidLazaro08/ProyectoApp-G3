package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginlayout.data.DBHelper
import com.example.loginlayout.ui.OfferAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private var isAdmin = false
    private var username = ""
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""
        db = DBHelper(this)

        // Saludo de bienvenida
        val txtWelcome = findViewById<TextView>(R.id.txtWelcomeName)
        txtWelcome.text = "Hola, $username"
        val txtHomeUser = findViewById<TextView?>(R.id.txtHomeUsername)
        txtHomeUser?.text = username

        // Carga las ofertas en horizontal
        val offers = db.getDiscountedProducts()
        if (offers.isNotEmpty()) {
            val recycler = findViewById<RecyclerView>(R.id.recyclerHomeOffers)
            recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recycler.adapter = OfferAdapter(this, offers) { p -> openDetail(p) }
        }

        // Botones de acceso rápido
        findViewById<Button>(R.id.btnHomeTienda).setOnClickListener {
            startActivity(Intent(this, EcommerceActivity::class.java).extras(isAdmin, username))
        }
        findViewById<Button>(R.id.btnHomeLibrary).setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java).extras(isAdmin, username))
        }
        findViewById<Button>(R.id.btnHomeOfertas).setOnClickListener {
            startActivity(Intent(this, EcommerceActivity::class.java).extras(isAdmin, username))
        }

        setupBottomNav()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: username
        findViewById<TextView>(R.id.txtWelcomeName).text = "Hola, $username"
    }

    private fun Intent.extras(admin: Boolean, user: String): Intent {
        putExtra("isAdmin", admin); putExtra("username", user); return this
    }

    private fun openDetail(p: com.example.loginlayout.data.Product) {
        startActivity(Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("productId", p.id)
            putExtra("nombreJuego", p.title)
            putExtra("categoriaJuego", p.category)
            putExtra("descripcionJuego", p.description)
            putExtra("precioJuego", p.price)
            putExtra("imagenPath", p.imagePath)
            putExtra("imageRes", p.imageRes)
            putExtra("isAdmin", isAdmin)
            putExtra("username", username)
        })
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_store -> { startActivity(Intent(this, EcommerceActivity::class.java).extras(isAdmin, username)); true }
                R.id.nav_library -> { startActivity(Intent(this, CatalogActivity::class.java).extras(isAdmin, username)); true }
                R.id.nav_cart -> { startActivity(Intent(this, CartActivity::class.java).extras(isAdmin, username)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java).extras(isAdmin, username)); true }
                else -> false
            }
        }
    }
}
