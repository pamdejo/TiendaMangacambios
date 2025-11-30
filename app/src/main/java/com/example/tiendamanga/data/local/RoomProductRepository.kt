package com.example.tiendamanga.com.example.tiendamanga.data.local

class RoomProductRepository(context: android.content.Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.productDao()

    suspend fun getAllProducts(): List<ProductEntity> {
        return dao.getAll()
    }

    suspend fun addProduct(
        name: String,
        price: Double,
        stock: Int,
        imageUrl: String? = null
    ) {
        dao.insert(
            ProductEntity(
                name = name,
                price = price,
                stock = stock,
                imageUrl = imageUrl
            )
        )
    }

    suspend fun updateProduct(product: ProductEntity) {
        dao.update(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        dao.delete(product)
    }

    suspend fun search(query: String): List<ProductEntity> {
        return dao.searchByName(query)
    }
}