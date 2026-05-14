package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginlayout.data.DBHelper
import com.example.loginlayout.data.Product
import com.example.loginlayout.ui.ProductAdapter

class CatalogActivity : AppCompatActivity() {

    // Activity que muestra productos desde la base local
    private lateinit var db: DBHelper
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        db = DBHelper(this)

        // Se insertan demos si la tabla está vacía
        if (db.getAllProducts().isEmpty()) {
            seedSampleProducts()
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(this, db.getAllProducts(), onAddToCart = { p ->
            db.addToCart(p.id)
            startActivity(Intent(this, CartActivity::class.java))
        }, onOpenDetail = { p ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("nombreJuego", p.title)
            intent.putExtra("categoriaJuego", p.category)
            intent.putExtra("descripcionJuego", p.description)
            intent.putExtra("precioJuego", p.price)
            intent.putExtra("imagenPath", p.imagePath)
            intent.putExtra("imageRes", p.imageRes)
            startActivity(intent)
        })

        recycler.adapter = adapter

        findViewById<android.widget.Button>(R.id.btnGoCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    // Inserta 3 juegos de ejemplo
    private fun seedSampleProducts() {
        val p1 = Product(title = "NEON STREETS REDUX", category = "Adventure · SNES", description = "The definitive 16-bit synthwave odyssey with retro arcade vibes.", price = 19.99, imageRes = R.drawable.neon_streets, seller = "retrodev")
        val p2 = Product(title = "SHADOW BLADE DX", category = "Adventure · FREE", description = "A fast retro action game with dark pixel art and classic arcade combat.", price = 0.0, imageRes = R.drawable.shadow_blade, seller = "retrodev")
        val p3 = Product(title = "RETRO STORM", category = "Fighting · Arcade", description = "A fighting game inspired by classic arcade machines and neon battles.", price = 29.99, imageRes = R.drawable.retro_storm, seller = "retrodev")
        db.insertProduct(p1)
        db.insertProduct(p2)
        db.insertProduct(p3)
    }

    // Refresca la lista al volver a primer plano
    override fun onResume() {
        super.onResume()
        val items = db.getAllProducts()
        adapter.update(items)
    }
}