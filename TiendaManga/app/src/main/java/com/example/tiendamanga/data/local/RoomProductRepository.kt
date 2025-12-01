package com.example.tiendamanga.data.local

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomProductRepository(context: Context) {


    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "products.db"
    ).build()

    private val dao: ProductDao = db.productDao()

    suspend fun getAllProducts(): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            dao.getAll()
        }
    }

    suspend fun addProduct(
        name: String,
        price: Double,
        stock: Int,
        imageUrl: String?,
        category: String? = null
    ) {
        val entity = ProductEntity(
            id = 0,
            name = name,
            price = price,
            stock = stock,
            imageUrl = imageUrl,
            category = category
        )
        withContext(Dispatchers.IO) {
            dao.insert(entity)
        }
    }

    suspend fun updateProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            dao.update(product)
        }
    }

    suspend fun deleteProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            dao.delete(product)
        }
    }
}