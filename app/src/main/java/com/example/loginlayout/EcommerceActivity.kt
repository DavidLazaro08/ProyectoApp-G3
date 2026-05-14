package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class EcommerceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

        val imgNeonHome = findViewById<ImageView>(R.id.imgNeonStreets)

        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        val btnCart = findViewById<Button>(R.id.btnCart)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        // admin button (added dynamically if user is admin)
        var btnAdmin: Button? = null

        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        // Si es admin añadimos un botón sencillo
        if (isAdmin) {
            btnAdmin = Button(this).apply {
                text = "PANEL ADMIN"
                setBackgroundColor(android.graphics.Color.parseColor("#A020F0"))
                setTextColor(android.graphics.Color.WHITE)
            }
            val root = findViewById<android.widget.LinearLayout>(R.id.rootEcommerce)
            root.addView(btnAdmin, root.childCount - 1)
        }

        imgNeonHome.setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)

            intent.putExtra("nombreJuego", "NEON STREETS REDUX")
            intent.putExtra("categoriaJuego", "Adventure · SNES")
            intent.putExtra(
                "descripcionJuego",
                "The definitive 16-bit synthwave odyssey with retro arcade vibes."
            )
            intent.putExtra("precioJuego", 19.99)
            intent.putExtra("imagenJuego", R.drawable.neon_streets)

            startActivity(intent)
        }

        btnLibrary.setOnClickListener {
            val intent = Intent(this, CatalogActivity::class.java)
            startActivity(intent)
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            finish()
        }

        btnAdmin?.setOnClickListener {
            val i = Intent(this, AdminUploadActivity::class.java)
            startActivity(i)
        }
    }
}