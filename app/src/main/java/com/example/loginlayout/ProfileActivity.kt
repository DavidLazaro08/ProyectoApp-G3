package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private var isAdmin = false
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""

        val db = DBHelper(this)
        val purchasedCount = db.getPurchasedProducts().size

        // Inicial del nombre como avatar
        findViewById<TextView>(R.id.txtProfileAvatar).text = username.take(1).uppercase()
        findViewById<TextView>(R.id.txtProfileName).text = username
        findViewById<TextView>(R.id.txtProfileRole).text = if (isAdmin) "Administrador" else "Usuario"
        findViewById<TextView>(R.id.txtGameCount).text = "$purchasedCount"

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        setupBottomNav()
    }

    private fun Intent.extras(admin: Boolean, user: String): Intent {
        putExtra("isAdmin", admin); putExtra("username", user); return this
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java).extras(isAdmin, username)); true }
                R.id.nav_store -> { startActivity(Intent(this, EcommerceActivity::class.java).extras(isAdmin, username)); true }
                R.id.nav_library -> { startActivity(Intent(this, CatalogActivity::class.java).extras(isAdmin, username)); true }
                R.id.nav_cart -> { startActivity(Intent(this, CartActivity::class.java).extras(isAdmin, username)); true }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}
