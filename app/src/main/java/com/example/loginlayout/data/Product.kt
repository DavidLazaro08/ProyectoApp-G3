
package com.example.loginlayout.data

// Modelo simple para un producto
data class Product(
    var id: Int = 0,
    var title: String,
    var category: String,
    var description: String,
    var price: Double,
    var discount: Int = 0,       // % de descuento
    var imageRes: Int = 0,       // drawable de recurso
    var imagePath: String? = null, // ruta de imagen del usuario
    var seller: String? = null,
    var createdAt: Long = System.currentTimeMillis()
)
