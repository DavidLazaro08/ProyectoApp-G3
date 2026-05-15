package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper

class CheckoutActivity : AppCompatActivity() {

    private var isAdmin = false
    private var username = ""
    private var currentStep = 1
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        isAdmin = intent.getBooleanExtra("isAdmin", false)
        username = intent.getStringExtra("username") ?: ""
        db = DBHelper(this)

        showStep(1)
    }

    private fun showStep(step: Int) {
        currentStep = step

        findViewById<View>(R.id.layoutStep1).visibility = if (step == 1) View.VISIBLE else View.GONE
        findViewById<View>(R.id.layoutStep2).visibility = if (step == 2) View.VISIBLE else View.GONE
        findViewById<View>(R.id.layoutStep3).visibility = if (step == 3) View.VISIBLE else View.GONE

        // Actualiza los pasos del indicador
        listOf(R.id.stepCircle1, R.id.stepCircle2, R.id.stepCircle3).forEachIndexed { idx, id ->
            val tv = findViewById<TextView>(id)
            tv.setBackgroundResource(if (idx + 1 <= step) R.drawable.step_active else R.drawable.step_inactive)
        }

        when (step) {
            1 -> setupStep1()
            2 -> setupStep2()
            3 -> setupStep3()
        }
    }

    private fun setupStep1() {
        val cart = db.getCartItems()
        val sb = StringBuilder()
        var subtotal = 0.0
        for ((pid, qty) in cart) {
            val p = db.getProductById(pid) ?: continue
            val price = if (p.price == 0.0) "GRATIS" else "€%.2f".format(p.price)
            sb.appendLine("${p.title}   $price")
            subtotal += p.price * qty
        }
        if (cart.isEmpty()) sb.append("El carrito está vacío")

        val tax = subtotal * 0.21
        val total = subtotal + tax

        findViewById<TextView>(R.id.txtOrderItems).text = sb.toString().trimEnd()
        findViewById<TextView>(R.id.txtCheckoutSubtotal).text = "Subtotal: €%.2f".format(subtotal)
        findViewById<TextView>(R.id.txtCheckoutTax).text = "IVA (21%%): €%.2f".format(tax)
        findViewById<TextView>(R.id.txtCheckoutTotal).text = "TOTAL: €%.2f".format(total)

        findViewById<Button>(R.id.btnGoPayment).setOnClickListener { showStep(2) }
    }

    private fun setupStep2() {
        val etCardNumber = findViewById<EditText>(R.id.etCardNumber)
        val etCardName = findViewById<EditText>(R.id.etCardName)
        val etCardExpiry = findViewById<EditText>(R.id.etCardExpiry)
        val etCardCvc = findViewById<EditText>(R.id.etCardCvc)
        val txtPayError = findViewById<TextView>(R.id.txtPayError)

        // Valida los datos de la tarjeta
        val btn = findViewById<Button>(R.id.btnConfirmPurchase)
        btn.setOnClickListener {
            val cardNum = etCardNumber.text.toString().replace(" ", "")
            val name = etCardName.text.toString().trim()
            val expiry = etCardExpiry.text.toString().trim()
            val cvc = etCardCvc.text.toString().trim()

            when {
                cardNum.length < 12 -> {
                    txtPayError.text = "Número de tarjeta inválido (mín. 12 dígitos)"
                    txtPayError.visibility = View.VISIBLE
                }
                name.isEmpty() -> {
                    txtPayError.text = "Introduce el nombre del titular"
                    txtPayError.visibility = View.VISIBLE
                }
                expiry.isEmpty() -> {
                    txtPayError.text = "Introduce la fecha de caducidad"
                    txtPayError.visibility = View.VISIBLE
                }
                cvc.length < 3 -> {
                    txtPayError.text = "CVC inválido (mín. 3 dígitos)"
                    txtPayError.visibility = View.VISIBLE
                }
                else -> {
                    txtPayError.visibility = View.GONE
                    db.purchaseCart()
                    showStep(3)
                }
            }
        }
    }

    private fun setupStep3() {
        findViewById<Button>(R.id.btnGoHome).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("isAdmin", isAdmin)
                putExtra("username", username)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finishAffinity()
        }
    }

    @Deprecated("Deprecated")
    override fun onBackPressed() {
        if (currentStep > 1) showStep(currentStep - 1)
        else super.onBackPressed()
    }
}
