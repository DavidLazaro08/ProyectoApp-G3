package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EcommerceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

        val btnLibrary = findViewById<Button>(R.id.btnLibrary)
        val btnCart = findViewById<Button>(R.id.btnCart)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

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
    }
}