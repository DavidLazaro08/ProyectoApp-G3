
package com.example.loginlayout.data

// Modelo simple para un producto
data class Product(
    var id: Int = 0,
    var title: String,
    var category: String,
    var description: String,
    var price: Double,
    var imageRes: Int = 0, // id de recurso si aplica
    var imagePath: String? = null, // uri si se seleccionó imagen
    var seller: String? = null,
    var createdAt: Long = System.currentTimeMillis()
)
