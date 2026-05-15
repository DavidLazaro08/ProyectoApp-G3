package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginlayout.data.DBHelper
import com.example.loginlayout.data.Product
import com.example.loginlayout.ui.OfferAdapter
import com.example.loginlayout.ui.ProductAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class EcommerceActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var adapter: ProductAdapter
    private var isAdmin = false
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""

        db = DBHelper(this)

        findViewById<TextView>(R.id.txtHeaderUsername).text = username

        val imgFeatured = findViewById<ImageView>(R.id.imgNeonStreets)
        imgFeatured.setOnClickListener {
            val featured = db.getAllProducts().lastOrNull()
            openDetail(
                featured?.id ?: -1,
                featured?.title ?: "NEON STREETS REDUX",
                featured?.category ?: "Adventure · SNES",
                featured?.description ?: "The definitive 16-bit synthwave odyssey with retro arcade vibes.",
                featured?.price ?: 19.99,
                null,
                R.drawable.neon_streets
            )
        }

        val btnAdmin = findViewById<Button>(R.id.btnAdminPanel)
        if (isAdmin) {
            btnAdmin.visibility = View.VISIBLE
            btnAdmin.setOnClickListener {
                startActivity(Intent(this, AdminUploadActivity::class.java))
            }
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerStore)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.isNestedScrollingEnabled = false

        adapter = ProductAdapter(
            context = this,
            items = db.getAllProducts(),
            onAddToCart = { p ->
                db.addToCart(p.id)
                startActivity(Intent(this, CartActivity::class.java).apply {
                    putExtra("isAdmin", isAdmin)
                    putExtra("username", username)
                })
            },
            onOpenDetail = { p ->
                openDetail(p.id, p.title, p.category, p.description, p.price, p.imagePath, p.imageRes)
            }
        )
        recycler.adapter = adapter

        // Carga las ofertas en horizontal
        val recyclerOffers = findViewById<RecyclerView?>(R.id.recyclerOffers)
        if (recyclerOffers != null) {
            val offers = db.getDiscountedProducts()
            recyclerOffers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerOffers.adapter = OfferAdapter(this, offers) { p ->
                openDetail(p.id, p.title, p.category, p.description, p.price, p.imagePath, p.imageRes)
            }
        }

        setupBottomNav(R.id.nav_store)
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: username
        findViewById<TextView>(R.id.txtHeaderUsername).text = username
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.update(db.getAllProducts())
        }
    }

    private fun openDetail(
        productId: Int, title: String, category: String,
        desc: String, price: Double, imagePath: String?, imageRes: Int
    ) {
        startActivity(Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("productId", productId)
            putExtra("nombreJuego", title)
            putExtra("categoriaJuego", category)
            putExtra("descripcionJuego", desc)
            putExtra("precioJuego", price)
            putExtra("imagenPath", imagePath)
            putExtra("imageRes", imageRes)
            putExtra("isAdmin", isAdmin)
            putExtra("username", username)
        })
    }

    private fun setupBottomNav(selectedId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = selectedId
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    })
                    true
                }
                R.id.nav_store -> true
                R.id.nav_library -> {
                    startActivity(Intent(this, CatalogActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
                    })
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java).apply {
                        putExtra("isAdmin", isAdmin); putExtra("username", username)
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
