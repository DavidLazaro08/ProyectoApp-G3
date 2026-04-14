package com.example.loginlayout

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EcommerceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

        val botonVolver = findViewById<Button>(R.id.btnVolver)

        botonVolver.setOnClickListener {
            finish()
        }
    }
}