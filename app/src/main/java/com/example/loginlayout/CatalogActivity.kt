package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val btnAddGame1 = findViewById<Button>(R.id.btnAddGame1)
        val btnAddGame2 = findViewById<Button>(R.id.btnAddGame2)
        val btnAddGame3 = findViewById<Button>(R.id.btnAddGame3)
        val btnGoCart = findViewById<Button>(R.id.btnGoCart)

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

    private fun abrirCarrito(nombreJuego: String, precioJuego: Double) {
        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra("nombreJuego", nombreJuego)
        intent.putExtra("precioJuego", precioJuego)
        startActivity(intent)
    }
}