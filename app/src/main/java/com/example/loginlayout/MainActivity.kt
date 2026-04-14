package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botonLogin = findViewById<Button>(R.id.btnLogin)

        botonLogin.setOnClickListener {
            val intent = Intent(this, EcommerceActivity::class.java)
            startActivity(intent)
        }
    }
}