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
        val etLogin = findViewById<android.widget.EditText>(R.id.etLogin)
        val etPassword = findViewById<android.widget.EditText>(R.id.etPassword)

        // Autenticación local simple para el proyecto
        botonLogin.setOnClickListener {
            val user = etLogin.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            val intent = Intent(this, EcommerceActivity::class.java)
            if (user == "admin" && pass == "admin123") {
                intent.putExtra("isAdmin", true)
            } else {
                intent.putExtra("isAdmin", false)
            }
            startActivity(intent)
        }
    }
}