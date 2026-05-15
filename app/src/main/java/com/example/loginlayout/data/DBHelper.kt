package com.example.loginlayout.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "retrostore.db"
        const val DATABASE_VERSION = 3

        const val TABLE_PRODUCTS = "products"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_CATEGORY = "category"
        const val COL_DESC = "description"
        const val COL_PRICE = "price"
        const val COL_IMAGE_RES = "image_res"
        const val COL_IMAGE_PATH = "image_path"
        const val COL_SELLER = "seller"
        const val COL_CREATED_AT = "created_at"

        const val TABLE_CART = "cart"
        const val COL_PRODUCT_ID = "product_id"
        const val COL_QUANTITY = "quantity"

        const val TABLE_PURCHASES = "purchases"
        const val COL_PURCHASED_AT = "purchased_at"

        const val TABLE_USERS = "users"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
        const val COL_IS_ADMIN = "is_admin"
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE $TABLE_PRODUCTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT,
                $COL_CATEGORY TEXT,
                $COL_DESC TEXT,
                $COL_PRICE REAL,
                $COL_IMAGE_RES INTEGER,
                $COL_IMAGE_PATH TEXT,
                $COL_SELLER TEXT,
                $COL_CREATED_AT INTEGER
            )"""
        )
        db.execSQL(
            """CREATE TABLE $TABLE_CART (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRODUCT_ID INTEGER,
                $COL_QUANTITY INTEGER
            )"""
        )
        db.execSQL(
            """CREATE TABLE $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE,
                $COL_PASSWORD TEXT,
                $COL_IS_ADMIN INTEGER DEFAULT 0
            )"""
        )
        db.execSQL(
            """CREATE TABLE $TABLE_PURCHASES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRODUCT_ID INTEGER,
                $COL_PURCHASED_AT INTEGER
            )"""
        )
        // Usuarios predeterminados: admin/admin123 y user/1234
        db.insert(TABLE_USERS, null, ContentValues().apply {
            put(COL_USERNAME, "admin")
            put(COL_PASSWORD, hashPassword("admin123"))
            put(COL_IS_ADMIN, 1)
        })
        db.insert(TABLE_USERS, null, ContentValues().apply {
            put(COL_USERNAME, "user")
            put(COL_PASSWORD, hashPassword("1234"))
            put(COL_IS_ADMIN, 0)
        })
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PURCHASES")
        onCreate(db)
    }

    // Valida credenciales: devuelve Pair(isValid, isAdmin)
    fun validateUser(username: String, password: String): Pair<Boolean, Boolean> {
        val db = readableDatabase
        val hashed = hashPassword(password)
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_IS_ADMIN),
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username.trim(), hashed),
            null, null, null
        )
        cursor.use {
            return if (it.moveToFirst()) {
                Pair(true, it.getInt(it.getColumnIndexOrThrow(COL_IS_ADMIN)) == 1)
            } else {
                Pair(false, false)
            }
        }
    }

    // Inserta un producto en la tabla
    fun insertProduct(p: Product): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_TITLE, p.title)
            put(COL_CATEGORY, p.category)
            put(COL_DESC, p.description)
            put(COL_PRICE, p.price)
            put(COL_IMAGE_RES, p.imageRes)
            put(COL_IMAGE_PATH, p.imagePath)
            put(COL_SELLER, p.seller)
            put(COL_CREATED_AT, p.createdAt)
        }
        return db.insert(TABLE_PRODUCTS, null, cv)
    }

    // Devuelve todos los productos ordenados por fecha
    fun getAllProducts(): List<Product> {
        val db = readableDatabase
        val list = mutableListOf<Product>()
        val cursor: Cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, "$COL_CREATED_AT DESC")
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val p = Product(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                        category = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_DESC)),
                        price = it.getDouble(it.getColumnIndexOrThrow(COL_PRICE)),
                        imageRes = it.getInt(it.getColumnIndexOrThrow(COL_IMAGE_RES)),
                        imagePath = it.getString(it.getColumnIndexOrThrow(COL_IMAGE_PATH)),
                        seller = it.getString(it.getColumnIndexOrThrow(COL_SELLER)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_CREATED_AT))
                    )
                    list.add(p)
                } while (it.moveToNext())
            }
        }
        return list
    }

    // Obtiene un producto por id
    fun getProductById(id: Int): Product? {
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null, "$COL_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                return Product(
                    id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                    category = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY)),
                    description = it.getString(it.getColumnIndexOrThrow(COL_DESC)),
                    price = it.getDouble(it.getColumnIndexOrThrow(COL_PRICE)),
                    imageRes = it.getInt(it.getColumnIndexOrThrow(COL_IMAGE_RES)),
                    imagePath = it.getString(it.getColumnIndexOrThrow(COL_IMAGE_PATH)),
                    seller = it.getString(it.getColumnIndexOrThrow(COL_SELLER)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(COL_CREATED_AT))
                )
            }
        }
        return null
    }

    // Añade un producto al carrito
    fun addToCart(productId: Int, quantity: Int = 1) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_PRODUCT_ID, productId)
            put(COL_QUANTITY, quantity)
        }
        db.insert(TABLE_CART, null, cv)
    }

    // Devuelve pares (productId, quantity) del carrito
    fun getCartItems(): List<Pair<Int, Int>> {
        val db = readableDatabase
        val list = mutableListOf<Pair<Int, Int>>()
        val cursor = db.query(TABLE_CART, null, null, null, null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val pid = it.getInt(it.getColumnIndexOrThrow(COL_PRODUCT_ID))
                    val qty = it.getInt(it.getColumnIndexOrThrow(COL_QUANTITY))
                    list.add(Pair(pid, qty))
                } while (it.moveToNext())
            }
        }
        return list
    }

    // Limpia el carrito (operación simple)
    fun clearCart() {
        val db = writableDatabase
        db.delete(TABLE_CART, null, null)
    }

    // Mueve los items del carrito a compras y vacía el carrito
    fun purchaseCart() {
        val db = writableDatabase
        val now = System.currentTimeMillis()
        val cartItems = getCartItems()
        db.beginTransaction()
        try {
            for ((productId, _) in cartItems) {
                // Insertar en compras si no está ya
                val exists = db.query(
                    TABLE_PURCHASES, arrayOf(COL_ID),
                    "$COL_PRODUCT_ID = ?", arrayOf(productId.toString()),
                    null, null, null
                ).use { it.count > 0 }
                if (!exists) {
                    db.insert(TABLE_PURCHASES, null, ContentValues().apply {
                        put(COL_PRODUCT_ID, productId)
                        put(COL_PURCHASED_AT, now)
                    })
                }
            }
            db.delete(TABLE_CART, null, null)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    // Devuelve los productos comprados (biblioteca)
    fun getPurchasedProducts(): List<Product> {
        val db = readableDatabase
        val list = mutableListOf<Product>()
        val cursor = db.rawQuery(
            """SELECT p.* FROM $TABLE_PRODUCTS p
               INNER JOIN $TABLE_PURCHASES pu ON p.$COL_ID = pu.$COL_PRODUCT_ID
               ORDER BY pu.$COL_PURCHASED_AT DESC""", null
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(Product(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                        category = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_DESC)),
                        price = it.getDouble(it.getColumnIndexOrThrow(COL_PRICE)),
                        imageRes = it.getInt(it.getColumnIndexOrThrow(COL_IMAGE_RES)),
                        imagePath = it.getString(it.getColumnIndexOrThrow(COL_IMAGE_PATH)),
                        seller = it.getString(it.getColumnIndexOrThrow(COL_SELLER)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_CREATED_AT))
                    ))
                } while (it.moveToNext())
            }
        }
        return list
    }
}
