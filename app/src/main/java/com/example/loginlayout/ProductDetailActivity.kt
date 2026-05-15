package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val imgProducto = findViewById<ImageView>(R.id.imgProducto)
        val txtTitulo = findViewById<TextView>(R.id.txtTitulo)
        val txtCategoria = findViewById<TextView>(R.id.txtCategoria)
        val txtDescripcion = findViewById<TextView>(R.id.txtDescripcion)
        val txtPrecio = findViewById<TextView>(R.id.txtPrecio)
        val btnComprar = findViewById<Button>(R.id.btnComprar)
        val btnVolver = findViewById<Button>(R.id.btnVolverDetalle)

        val productId = intent.getIntExtra("productId", -1)
        val nombreJuego = intent.getStringExtra("nombreJuego") ?: "NEON STREETS REDUX"
        val categoriaJuego = intent.getStringExtra("categoriaJuego") ?: "Adventure · SNES"
        val descripcionJuego = intent.getStringExtra("descripcionJuego")
            ?: "The definitive 16-bit synthwave odyssey with retro arcade vibes."
        val precioJuego = intent.getDoubleExtra("precioJuego", 19.99)
        val imagenPath = intent.getStringExtra("imagenPath")
        val imagenRes = intent.getIntExtra("imageRes", intent.getIntExtra("imagenJuego", R.drawable.neon_streets))
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        val username = intent.getStringExtra("username") ?: ""

        if (!imagenPath.isNullOrEmpty()) {
            try {
                imgProducto.setImageURI(android.net.Uri.parse(imagenPath))
            } catch (e: Exception) {
                imgProducto.setImageResource(if (imagenRes != 0) imagenRes else R.drawable.neon_streets)
            }
        } else {
            imgProducto.setImageResource(if (imagenRes != 0) imagenRes else R.drawable.neon_streets)
        }

        txtTitulo.text = nombreJuego
        txtCategoria.text = categoriaJuego
        txtDescripcion.text = descripcionJuego
        txtPrecio.text = if (precioJuego == 0.0) "GRATIS" else String.format("€%.2f", precioJuego)

        btnComprar.setOnClickListener {
            val db = DBHelper(this)
            if (productId != -1) {
                db.addToCart(productId)
            } else {
                // Fallback: busca por título si no tenemos id
                val found = db.getAllProducts().find { it.title == nombreJuego }
                if (found != null) db.addToCart(found.id)
            }
            startActivity(Intent(this, CartActivity::class.java).apply {
                putExtra("isAdmin", isAdmin)
                putExtra("username", username)
            })
        }

        btnVolver.setOnClickListener { finish() }
    }
}
