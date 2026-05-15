package com.example.loginlayout

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginlayout.data.DBHelper
import com.example.loginlayout.data.Product

// Subida de juegos por el administrador
class AdminUploadActivity : AppCompatActivity() {

    private val PICK_IMAGE = 1001
    private var selectedImageUri: Uri? = null
    private lateinit var imgPreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_upload)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        imgPreview = findViewById(R.id.imgPreview)

        val btnPick = findViewById<Button>(R.id.btnPickImage)
        val btnSave = findViewById<Button>(R.id.btnSaveProduct)

        // Abre el selector de imágenes
        btnPick.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            }
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Guarda el juego en la BD
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val desc = etDescription.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Introduce un título", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = DBHelper(this)
            val prod = Product(
                title = title,
                category = category,
                description = desc,
                price = price,
                imageRes = 0,
                imagePath = selectedImageUri?.toString(),
                seller = "admin"
            )
            db.insertProduct(prod)
            Toast.makeText(this, "Juego guardado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Recibe la imagen seleccionada
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                selectedImageUri = uri
                try {
                    imgPreview.setImageURI(uri)
                } catch (e: Exception) { /* keep placeholder */ }
                // Guarda permiso de lectura de la imagen
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
