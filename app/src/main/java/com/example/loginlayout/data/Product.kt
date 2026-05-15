package com.example.loginlayout.data

/*
 * Modelo de datos para los productos de Retro Store.
 * Se usa tanto para los juegos cargados inicialmente como para los añadidos desde administración.
 */
data class Product(
    var id: Int = 0,
    var title: String,
    var category: String,
    var description: String,
    var price: Double,
    var discount: Int = 0,
    var imageRes: Int = 0,
    var imagePath: String? = null,
    var seller: String? = null,
    var createdAt: Long = System.currentTimeMillis()
)