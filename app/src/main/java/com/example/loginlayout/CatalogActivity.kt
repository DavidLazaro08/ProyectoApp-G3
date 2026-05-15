package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginlayout.data.DBHelper
import com.example.loginlayout.ui.GameGridAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class CatalogActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var gridAdapter: GameGridAdapter
    private var isAdmin = false
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""

        db = DBHelper(this)

        findViewById<TextView>(R.id.txtLibraryUser).text = username

        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
        val txtEmpty = findViewById<TextView>(R.id.txtLibraryEmpty)
        recycler.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.grid_columns))

        val purchased = db.getPurchasedProducts()
        txtEmpty?.visibility = if (purchased.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (purchased.isEmpty()) View.GONE else View.VISIBLE

        gridAdapter = GameGridAdapter(this, purchased) { p ->
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
        recycler.adapter = gridAdapter

        setupBottomNav()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: username
        findViewById<TextView>(R.id.txtLibraryUser).text = username
    }

    override fun onResume() {
        super.onResume()
        if (::gridAdapter.isInitialized) {
            val purchased = db.getPurchasedProducts()
            gridAdapter.update(purchased)
            val txtEmpty = findViewById<TextView>(R.id.txtLibraryEmpty)
            val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
            txtEmpty?.visibility = if (purchased.isEmpty()) View.VISIBLE else View.GONE
            recycler?.visibility = if (purchased.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_library
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    })
                    true
                }
                R.id.nav_store -> {
                    startActivity(Intent(this, EcommerceActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin)
                        putExtra("username", username)
                    })
                    true
                }
                R.id.nav_library -> true
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin)
                        putExtra("username", username)
                    })
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    })
                    true
                }
                else -> false
            }
        }
    }
}
