package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtError = findViewById<TextView>(R.id.txtLoginError)

        val db = DBHelper(this)

        btnLogin.setOnClickListener {
            val username = etLogin.text.toString().trim()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                txtError.visibility = View.VISIBLE
                txtError.text = "Introduce usuario y contraseña"
                return@setOnClickListener
            }

            val (isValid, isAdmin) = db.validateUser(username, password)

            if (isValid) {
                txtError.visibility = View.GONE
                startActivity(Intent(this, SteamGuardActivity::class.java).apply {
                    putExtra("isAdmin", isAdmin)
                    putExtra("username", username)
                })
            } else {
                txtError.visibility = View.VISIBLE
                txtError.text = "Usuario o contraseña incorrectos"
                etPassword.text.clear()
            }
        }
        val txtGoRegister = findViewById<TextView?>(R.id.txtGoRegister)
        txtGoRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}