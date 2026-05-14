package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
        val btnVolverDetalle = findViewById<Button>(R.id.btnVolverDetalle)

        val nombreJuego = intent.getStringExtra("nombreJuego") ?: "NEON STREETS REDUX"
        val categoriaJuego = intent.getStringExtra("categoriaJuego") ?: "Adventure · SNES"
        val descripcionJuego = intent.getStringExtra("descripcionJuego")
            ?: "The definitive 16-bit synthwave odyssey with retro arcade vibes."
        val precioJuego = intent.getDoubleExtra("precioJuego", 19.99)
        val imagenPath = intent.getStringExtra("imagenPath")
        val imagenRes = intent.getIntExtra("imageRes", intent.getIntExtra("imagenJuego", R.drawable.neon_streets))

        // Muestra imagen desde uri o recurso según esté disponible
        if (!imagenPath.isNullOrEmpty()) {
            imgProducto.setImageURI(android.net.Uri.parse(imagenPath))
        } else {
            imgProducto.setImageResource(imagenRes)
        }
        txtTitulo.text = nombreJuego
        txtCategoria.text = categoriaJuego
        txtDescripcion.text = descripcionJuego

        if (precioJuego == 0.0) {
            txtPrecio.text = "FREE"
        } else {
            txtPrecio.text = String.format("€%.2f", precioJuego)
        }

        btnComprar.setOnClickListener {
            val intentCarrito = Intent(this, CartActivity::class.java)
            intentCarrito.putExtra("nombreJuego", nombreJuego)
            intentCarrito.putExtra("precioJuego", precioJuego)
            startActivity(intentCarrito)
        }

        btnVolverDetalle.setOnClickListener {
            finish()
        }
    }
}