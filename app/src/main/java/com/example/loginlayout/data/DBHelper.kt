package com.example.loginlayout.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.loginlayout.R
import java.security.MessageDigest

/*
 * Gestiona la base de datos local de Retro Store.
 * Aquí se crean las tablas y se agrupan las operaciones principales
 * de usuarios, productos, carrito y compras.
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "retrostore.db"
        const val DATABASE_VERSION = 4

        const val TABLE_PRODUCTS = "products"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_CATEGORY = "category"
        const val COL_DESC = "description"
        const val COL_PRICE = "price"
        const val COL_DISCOUNT = "discount"
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
        db.execSQL("""CREATE TABLE $TABLE_PRODUCTS (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TITLE TEXT, $COL_CATEGORY TEXT, $COL_DESC TEXT,
            $COL_PRICE REAL, $COL_DISCOUNT INTEGER DEFAULT 0,
            $COL_IMAGE_RES INTEGER, $COL_IMAGE_PATH TEXT,
            $COL_SELLER TEXT, $COL_CREATED_AT INTEGER)""")

        db.execSQL("""CREATE TABLE $TABLE_CART (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_PRODUCT_ID INTEGER, $COL_QUANTITY INTEGER)""")

        db.execSQL("""CREATE TABLE $TABLE_PURCHASES (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_PRODUCT_ID INTEGER, $COL_PURCHASED_AT INTEGER)""")

        db.execSQL("""CREATE TABLE $TABLE_USERS (
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_USERNAME TEXT UNIQUE, $COL_PASSWORD TEXT, $COL_IS_ADMIN INTEGER DEFAULT 0)""")

        // Usuarios de prueba para poder acceder a la app desde el primer arranque.
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

        // Catálogo inicial de juegos de muestra.
        data class GameSeed(
            val title: String,
            val cat: String,
            val desc: String,
            val price: Double,
            val disc: Int,
            val imgRes: Int,
            val seller: String
        )

        val games = listOf(
            GameSeed("NEON STREETS REDUX", "Adventure", "The definitive 16-bit synthwave odyssey with retro arcade vibes.", 19.99, 0, R.drawable.neon_streets, "retrodev"),
            GameSeed("SHADOW BLADE DX", "Action", "A fast retro action game with dark pixel art and classic arcade combat.", 0.0, 0, R.drawable.shadow_blade, "retrodev"),
            GameSeed("RETRO STORM", "Fighting", "A fighting game inspired by classic arcade machines and neon battles.", 29.99, 0, R.drawable.retro_storm, "retrodev"),
            GameSeed("PIXEL DUNGEON X", "RPG", "Explora mazmorras generadas aleatoriamente en este clásico roguelike.", 14.99, 50, R.drawable.neon_streets, "retrodev"),
            GameSeed("GALAXY RAIDERS", "Shooter", "Defiende la galaxia en este shooter de desplazamiento lateral estilo arcade.", 9.99, 0, R.drawable.shadow_blade, "retrodev"),
            GameSeed("MEGA FORCE 2", "Platformer", "Plataformas de acción con más de 80 niveles y jefes finales épicos.", 12.99, 30, R.drawable.retro_storm, "retrodev"),
            GameSeed("CYBER QUEST", "RPG", "Un RPG cyberpunk por turnos con historia ramificada y múltiples finales.", 24.99, 0, R.drawable.neon_streets, "retrodev"),
            GameSeed("TURBO KART 64", "Racing", "Carreras de karts con pistas temáticas retro y modo multijugador local.", 14.99, 0, R.drawable.shadow_blade, "retrodev"),
            GameSeed("NINJA LEGENDS", "Action", "Domina el arte del ninjutsu en este hack-and-slash de acción frenética.", 19.99, 25, R.drawable.retro_storm, "retrodev"),
            GameSeed("SPACE BLASTERS", "Shooter", "Shooter espacial gratuito con oleadas infinitas de enemigos alienígenas.", 0.0, 0, R.drawable.shadow_blade, "retrodev"),
            GameSeed("DRAGON FIST", "Fighting", "Juego de lucha con 24 personajes y modo torneo online simulado.", 34.99, 40, R.drawable.neon_streets, "retrodev"),
            GameSeed("BEAT RUSH", "Rhythm", "Juego de ritmo con más de 100 canciones de música electrónica retro.", 7.99, 0, R.drawable.retro_storm, "retrodev")
        )

        val now = System.currentTimeMillis()

        for (game in games) {
            db.insert(TABLE_PRODUCTS, null, ContentValues().apply {
                put(COL_TITLE, game.title)
                put(COL_CATEGORY, game.cat)
                put(COL_DESC, game.desc)
                put(COL_PRICE, game.price)
                put(COL_DISCOUNT, game.disc)
                put(COL_IMAGE_RES, game.imgRes)
                put(COL_SELLER, game.seller)
                put(COL_CREATED_AT, now)
            })
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PURCHASES")
        onCreate(db)
    }

    fun validateUser(username: String, password: String): Pair<Boolean, Boolean> {
        val db = readableDatabase
        val hashed = hashPassword(password)

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_IS_ADMIN),
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username.trim(), hashed),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                Pair(true, it.getInt(it.getColumnIndexOrThrow(COL_IS_ADMIN)) == 1)
            } else {
                Pair(false, false)
            }
        }
    }

    fun registerUser(username: String, password: String): Boolean {
        return try {
            writableDatabase.insert(TABLE_USERS, null, ContentValues().apply {
                put(COL_USERNAME, username.trim())
                put(COL_PASSWORD, hashPassword(password))
                put(COL_IS_ADMIN, 0)
            }) != -1L
        } catch (e: Exception) {
            false
        }
    }

    fun insertProduct(p: Product): Long {
        val cv = ContentValues().apply {
            put(COL_TITLE, p.title)
            put(COL_CATEGORY, p.category)
            put(COL_DESC, p.description)
            put(COL_PRICE, p.price)
            put(COL_DISCOUNT, p.discount)
            put(COL_IMAGE_RES, p.imageRes)
            put(COL_IMAGE_PATH, p.imagePath)
            put(COL_SELLER, p.seller)
            put(COL_CREATED_AT, p.createdAt)
        }

        return writableDatabase.insert(TABLE_PRODUCTS, null, cv)
    }

    private fun cursorToProduct(cursor: Cursor) = Product(
        id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
        title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
        category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
        description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC)),
        price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
        discount = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DISCOUNT)),
        imageRes = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IMAGE_RES)),
        imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_PATH)),
        seller = cursor.getString(cursor.getColumnIndexOrThrow(COL_SELLER)),
        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
    )

    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()

        val cursor = readableDatabase.query(
            TABLE_PRODUCTS,
            null,
            null,
            null,
            null,
            null,
            "$COL_CREATED_AT ASC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(cursorToProduct(it))
                } while (it.moveToNext())
            }
        }

        return list
    }

    fun getDiscountedProducts(): List<Product> {
        val list = mutableListOf<Product>()

        val cursor = readableDatabase.query(
            TABLE_PRODUCTS,
            null,
            "$COL_DISCOUNT > 0",
            null,
            null,
            null,
            "$COL_DISCOUNT DESC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(cursorToProduct(it))
                } while (it.moveToNext())
            }
        }

        return list
    }

    fun getProductById(id: Int): Product? {
        val cursor = readableDatabase.query(
            TABLE_PRODUCTS,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) cursorToProduct(it) else null
        }
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        writableDatabase.insert(TABLE_CART, null, ContentValues().apply {
            put(COL_PRODUCT_ID, productId)
            put(COL_QUANTITY, quantity)
        })
    }

    fun getCartItems(): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()

        val cursor = readableDatabase.query(TABLE_CART, null, null, null, null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Pair(
                            it.getInt(it.getColumnIndexOrThrow(COL_PRODUCT_ID)),
                            it.getInt(it.getColumnIndexOrThrow(COL_QUANTITY))
                        )
                    )
                } while (it.moveToNext())
            }
        }

        return list
    }

    fun clearCart() {
        writableDatabase.delete(TABLE_CART, null, null)
    }

    fun purchaseCart() {
        val now = System.currentTimeMillis()
        val cartItems = getCartItems()
        val db = writableDatabase

        db.beginTransaction()

        try {
            for ((productId, _) in cartItems) {
                val exists = db.query(
                    TABLE_PURCHASES,
                    arrayOf(COL_ID),
                    "$COL_PRODUCT_ID = ?",
                    arrayOf(productId.toString()),
                    null,
                    null,
                    null
                ).use {
                    it.count > 0
                }

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

    fun getPurchasedProducts(): List<Product> {
        val list = mutableListOf<Product>()

        val cursor = readableDatabase.rawQuery(
            """SELECT p.* FROM $TABLE_PRODUCTS p
            INNER JOIN $TABLE_PURCHASES pu ON p.$COL_ID = pu.$COL_PRODUCT_ID
            ORDER BY pu.$COL_PURCHASED_AT DESC""",
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(cursorToProduct(it))
                } while (it.moveToNext())
            }
        }

        return list
    }
}