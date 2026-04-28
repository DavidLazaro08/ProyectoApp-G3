package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val btnAddGame1 = findViewById<Button>(R.id.btnAddGame1)
        val btnAddGame2 = findViewById<Button>(R.id.btnAddGame2)
        val btnAddGame3 = findViewById<Button>(R.id.btnAddGame3)
        val btnGoCart = findViewById<Button>(R.id.btnGoCart)

        val imgNeon = findViewById<ImageView>(R.id.imgNeonStreets)
        val imgShadow = findViewById<ImageView>(R.id.imgShadowBlade)
        val imgRetro = findViewById<ImageView>(R.id.imgRetroStorm)

        imgNeon.setOnClickListener {
            abrirDetalle(
                "NEON STREETS REDUX",
                "Adventure · SNES",
                "The definitive 16-bit synthwave odyssey with retro arcade vibes.",
                19.99,
                R.drawable.neon_streets
            )
        }

        imgShadow.setOnClickListener {
            abrirDetalle(
                "SHADOW BLADE DX",
                "Adventure · FREE",
                "A fast retro action game with dark pixel art and classic arcade combat.",
                0.00,
                R.drawable.shadow_blade
            )
        }

        imgRetro.setOnClickListener {
            abrirDetalle(
                "RETRO STORM",
                "Fighting · Arcade",
                "A fighting game inspired by classic arcade machines and neon battles.",
                29.99,
                R.drawable.retro_storm
            )
        }

        btnAddGame1.setOnClickListener {
            abrirCarrito("NEON STREETS REDUX", 19.99)
        }

        btnAddGame2.setOnClickListener {
            abrirCarrito("SHADOW BLADE DX", 0.00)
        }

        btnAddGame3.setOnClickListener {
            abrirCarrito("RETRO STORM", 29.99)
        }

        btnGoCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun abrirDetalle(
        nombreJuego: String,
        categoriaJuego: String,
        descripcionJuego: String,
        precioJuego: Double,
        imagenJuego: Int
    ) {
        val intent = Intent(this, ProductDetailActivity::class.java)

        intent.putExtra("nombreJuego", nombreJuego)
        intent.putExtra("categoriaJuego", categoriaJuego)
        intent.putExtra("descripcionJuego", descripcionJuego)
        intent.putExtra("precioJuego", precioJuego)
        intent.putExtra("imagenJuego", imagenJuego)

        startActivity(intent)
    }

    private fun abrirCarrito(nombreJuego: String, precioJuego: Double) {
        val intent = Intent(this, CartActivity::class.java)

        intent.putExtra("nombreJuego", nombreJuego)
        intent.putExtra("precioJuego", precioJuego)

        startActivity(intent)
    }
}