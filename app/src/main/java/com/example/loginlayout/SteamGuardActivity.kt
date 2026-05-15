package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SteamGuardActivity : AppCompatActivity() {

    private var isAdmin = false
    private var username = ""

    private val codes = listOf("R4X-7K2", "B9P-3M5", "G7Y-2Q8", "Z1C-9F3", "M8V-4L6")
    private var codeIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var txtCode: TextView
    private lateinit var progressBar: ProgressBar

    private var progressValue = 100
    private val tickMs = 50L  // update progress every 50ms
    private val periodMs = 5000L  // code changes every 5 seconds
    private val totalTicks = periodMs / tickMs

    private val tickRunnable = object : Runnable {
        override fun run() {
            progressValue -= (100.0 / totalTicks).toInt().coerceAtLeast(1)
            if (progressValue <= 0) {
                progressValue = 100
                codeIndex = (codeIndex + 1) % codes.size
                txtCode.text = codes[codeIndex]
            }
            progressBar.progress = progressValue
            handler.postDelayed(this, tickMs)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_guard)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""

        txtCode = findViewById(R.id.txtGuardCode)
        progressBar = findViewById(R.id.guardProgress)

        txtCode.text = codes[0]
        progressBar.max = 100
        progressBar.progress = 100

        handler.postDelayed(tickRunnable, tickMs)

        findViewById<Button>(R.id.btnApprove).setOnClickListener { goHome() }
    }

    private fun goHome() {
        handler.removeCallbacks(tickRunnable)
        startActivity(Intent(this, HomeActivity::class.java).apply {
            putExtra("isAdmin", isAdmin)
            putExtra("username", username)
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tickRunnable)
    }
}
