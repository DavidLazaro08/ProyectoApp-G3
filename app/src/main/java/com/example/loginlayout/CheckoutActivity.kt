package com.example.loginlayout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper
import java.util.Calendar

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

        val btnGoPayment = findViewById<Button>(R.id.btnGoPayment)
        if (cart.isEmpty()) {
            btnGoPayment.isEnabled = false
            btnGoPayment.alpha = 0.4f
            btnGoPayment.text = "CARRITO VACÍO"
        } else {
            btnGoPayment.isEnabled = true
            btnGoPayment.alpha = 1f
            btnGoPayment.text = "CONTINUAR AL PAGO"
            btnGoPayment.setOnClickListener { showStep(2) }
        }
    }

    private fun setupStep2() {
        val etCardNumber  = findViewById<EditText>(R.id.etCardNumber)
        val etCardName    = findViewById<EditText>(R.id.etCardName)
        val etCardExpiry  = findViewById<EditText>(R.id.etCardExpiry)
        val etCardCvc     = findViewById<EditText>(R.id.etCardCvc)
        val txtPayError   = findViewById<TextView>(R.id.txtPayError)
        val txtCardType   = findViewById<TextView>(R.id.txtCardType)
        val progressPay   = findViewById<ProgressBar>(R.id.progressPay)
        val previewNumber = findViewById<TextView>(R.id.previewCardNumber)
        val previewName   = findViewById<TextView>(R.id.previewCardName)
        val previewExpiry = findViewById<TextView>(R.id.previewCardExpiry)
        val previewType   = findViewById<TextView>(R.id.previewCardType)
        val btn           = findViewById<Button>(R.id.btnConfirmPurchase)

        // Auto-formato número de tarjeta: XXXX XXXX XXXX XXXX
        etCardNumber.addTextChangedListener(object : TextWatcher {
            private var formatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (formatting || s == null) return
                formatting = true
                val digits = s.toString().filter { it.isDigit() }.take(16)
                val formatted = digits.chunked(4).joinToString(" ")
                if (s.toString() != formatted) s.replace(0, s.length, formatted)
                val type = detectCardType(digits)
                txtCardType.text = type
                previewType.text = type
                previewNumber.text = if (digits.length >= 4)
                    "•••• •••• •••• ${digits.takeLast(4)}"
                else "•••• •••• •••• ••••"
                formatting = false
            }
        })

        // Auto-formato caducidad: MM/AA
        etCardExpiry.addTextChangedListener(object : TextWatcher {
            private var formatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (formatting || s == null) return
                formatting = true
                val digits = s.toString().filter { it.isDigit() }.take(4)
                val formatted = if (digits.length > 2) "${digits.take(2)}/${digits.drop(2)}" else digits
                if (s.toString() != formatted) s.replace(0, s.length, formatted)
                previewExpiry.text = if (formatted.isNotEmpty()) formatted else "MM/AA"
                formatting = false
            }
        })

        // Actualiza nombre en la preview en mayúsculas
        etCardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                previewName.text = if (s.isNullOrBlank()) "TITULAR" else s.toString().uppercase()
            }
        })

        btn.setOnClickListener {
            val cardNum = etCardNumber.text.toString().filter { it.isDigit() }
            val name    = etCardName.text.toString().trim()
            val expiry  = etCardExpiry.text.toString().trim()
            val cvc     = etCardCvc.text.toString().trim()

            val error = when {
                cardNum.length < 13     -> "Número de tarjeta inválido (mín. 13 dígitos)"
                cardNum.length > 19     -> "Número de tarjeta inválido (máx. 19 dígitos)"
                name.isEmpty()          -> "Introduce el nombre del titular"
                !validateExpiry(expiry) -> "Fecha de caducidad inválida o expirada"
                cvc.length < 3          -> "CVC inválido (mín. 3 dígitos)"
                else                    -> null
            }

            if (error != null) {
                txtPayError.text = error
                txtPayError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Estado de procesamiento
            txtPayError.visibility = View.GONE
            btn.isEnabled = false
            btn.text = "Procesando..."
            progressPay.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                db.purchaseCart()
                showStep(3)
            }, 1800)
        }
    }

    private fun setupStep3() {
        findViewById<Button>(R.id.btnGoHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                putExtra("isAdmin", isAdmin)
                putExtra("username", username)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
    }

    // Algoritmo de Luhn para validar el número de tarjeta
    private fun luhn(number: String): Boolean {
        val digits = number.filter { it.isDigit() }
        var sum = 0
        digits.reversed().forEachIndexed { i, c ->
            var n = c.digitToInt()
            if (i % 2 == 1) { n *= 2; if (n > 9) n -= 9 }
            sum += n
        }
        return sum % 10 == 0
    }

    private fun detectCardType(digits: String): String = when {
        digits.startsWith("4")                                              -> "VISA"
        digits.length >= 2 && digits.take(2).toInt() in 51..55            -> "MASTERCARD"
        digits.length >= 2 && (digits.startsWith("34") || digits.startsWith("37")) -> "AMEX"
        digits.length >= 4 && (digits.startsWith("6011") || digits.startsWith("65")) -> "DISCOVER"
        else -> ""
    }

    // Valida que la fecha no esté expirada
    private fun validateExpiry(expiry: String): Boolean {
        val parts = expiry.split("/")
        if (parts.size != 2 || parts[0].length != 2 || parts[1].length != 2) return false
        val month = parts[0].toIntOrNull() ?: return false
        val year  = parts[1].toIntOrNull() ?: return false
        if (month < 1 || month > 12) return false
        val cal      = Calendar.getInstance()
        val curYear  = cal.get(Calendar.YEAR) % 100
        val curMonth = cal.get(Calendar.MONTH) + 1
        return year > curYear || (year == curYear && month >= curMonth)
    }

    @Deprecated("Deprecated")
    override fun onBackPressed() {
        if (currentStep > 1) showStep(currentStep - 1)
        else super.onBackPressed()
    }
}
