package com.example.tiendamanga.com.example.tiendamanga

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.example.tiendamanga.com.example.tiendamanga.data.local.RoomProductRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.google.gson.Gson

class ProductJsBridge(context: Context) {

    private val repo = RoomProductRepository(context.applicationContext)
    private val gson = Gson()

    // JS llamará esto para obtener la lista de productos como JSON
    @JavascriptInterface
    fun getProductsJson(): String {
        return try {
            val products = runBlocking { repo.getAllProducts() }
            gson.toJson(products)
        } catch (e: Exception) {
            Log.e("ProductJsBridge", "Error getProductsJson", e)
            "[]"
        }
    }

    // Crear producto
    @JavascriptInterface
    fun createProduct(name: String, price: Double, stock: Int, imageUrl: String?) {
        GlobalScope.launch {
            try {
                repo.addProduct(name, price, stock, imageUrl)
            } catch (e: Exception) {
                Log.e("ProductJsBridge", "Error createProduct", e)
            }
        }
    }

    // Actualizar producto
    @JavascriptInterface
    fun updateProduct(id: Int, name: String, price: Double, stock: Int, imageUrl: String?) {
        GlobalScope.launch {
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

    // Eliminar producto
    @JavascriptInterface
    fun deleteProduct(id: Int) {
        GlobalScope.launch {
            try {
                val current = repo.getAllProducts().find { it.id == id } ?: return@launch
                repo.deleteProduct(current)
            } catch (e: Exception) {
                Log.e("ProductJsBridge", "Error deleteProduct", e)
            }
        }
    }
}