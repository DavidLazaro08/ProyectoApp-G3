package com.example.loginlayout

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper

/*
 * Pantalla de registro de nuevos usuarios.
 * Valida los datos introducidos y crea la cuenta en la base de datos local.
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUser = findViewById<EditText>(R.id.etRegUsername)
        val etPass = findViewById<EditText>(R.id.etRegPassword)
        val etConfirm = findViewById<EditText>(R.id.etRegConfirm)
        val txtError = findViewById<TextView>(R.id.txtRegError)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoLogin = findViewById<TextView>(R.id.btnGoLogin)

        btnRegister.setOnClickListener {
            val username = etUser.text.toString().trim()
            val password = etPass.text.toString()
            val confirm = etConfirm.text.toString()

            when {
                username.isEmpty() || password.isEmpty() -> {
                    txtError.text = "Rellena todos los campos"
                    txtError.visibility = View.VISIBLE
                }

                password.length < 4 -> {
                    txtError.text = "La contraseña debe tener al menos 4 caracteres"
                    txtError.visibility = View.VISIBLE
                }

                password != confirm -> {
                    txtError.text = "Las contraseñas no coinciden"
                    txtError.visibility = View.VISIBLE
                    etConfirm.text.clear()
                }

                else -> {
                    val db = DBHelper(this)
                    val success = db.registerUser(username, password)

                    if (success) {
                        Toast.makeText(
                            this,
                            "¡Cuenta creada! Ya puedes iniciar sesión.",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else {
                        txtError.text = "Ese nombre de usuario ya existe"
                        txtError.visibility = View.VISIBLE
                    }
                }
            }
        }

        // Volvemos al login sin crear cuenta.
        btnGoLogin.setOnClickListener {
            finish()
        }
    }
}