package com.example.tiendamanga

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.example.tiendamanga.data.local.RoomProductRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ProductJsBridge(context: Context) {

    private val repo = RoomProductRepository(context.applicationContext)
    private val gson = Gson()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    data class JsProductDTO(
        val id: Int,
        val name: String,
        val price: Double,
        val stock: Int,
        val imageUrl: String?,
        val category: String?
    )

    @JavascriptInterface
    fun getProductsJson(): String {
        return try {
            val products = runBlocking { repo.getAllProducts() }

            val jsList = products.map {
                JsProductDTO(
                    id = it.id,
                    name = it.name,
                    price = it.price,
                    stock = it.stock,
                    imageUrl = it.imageUrl,
                    category = it.category
                )
            }

            gson.toJson(jsList)
        } catch (e: Exception) {
            Log.e("ProductJsBridge", "Error getProductsJson", e)
            "[]"
        }
    }

    @JavascriptInterface
    fun createProduct(name: String, price: Double, stock: Int, imageUrl: String?) {
        ioScope.launch {
            try {
                repo.addProduct(name, price, stock, imageUrl)
            } catch (e: Exception) {
                Log.e("ProductJsBridge", "Error createProduct", e)
            }
        }
    }

    @JavascriptInterface
    fun updateProduct(id: Int, name: String, price: Double, stock: Int, imageUrl: String?) {
        ioScope.launch {
            try {
                val current = repo.getAllProducts().find { it.id == id } ?: return@launch
                val updated = current.copy(
                    name = name,
                    price = price,
                    stock = stock,
                    imageUrl = imageUrl
                )
                repo.updateProduct(updated)
            } catch (e: Exception) {
                Log.e("ProductJsBridge", "Error updateProduct", e)
            }
        }
    }

    @JavascriptInterface
    fun deleteProduct(id: Int) {
        ioScope.launch {
            try {
                val current = repo.getAllProducts().find { it.id == id } ?: return@launch
                repo.deleteProduct(current)
            } catch (e: Exception) {
                Log.e("ProductJsBridge", "Error deleteProduct", e)
            }
        }
    }
}