package com.example.tiendamanga

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ProductRepository {

    private const val PREFS_NAME = "user_products_prefs"
    private const val KEY_USER_PRODUCTS = "user_products"

    private val gson = Gson()


    private fun getUserProducts(context: Context): MutableList<ProductDTO> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_USER_PRODUCTS, null) ?: return mutableListOf()

        return try {
            val type = object : TypeToken<List<ProductDTO>>() {}.type
            val list: List<ProductDTO> = gson.fromJson(json, type) ?: emptyList()
            list.toMutableList()
        } catch (_: Exception) {
            mutableListOf()
        }
    }


    private fun saveUserProducts(context: Context, list: List<ProductDTO>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_USER_PRODUCTS, json).apply()
    }


    fun addUserProduct(context: Context, product: ProductDTO) {
        val current = getUserProducts(context)


        val nextId = (current.maxOfOrNull { it.id } ?: 1000) + 1
        val productWithId = product.copy(id = nextId)

        current.add(productWithId)
        saveUserProducts(context, current)
    }


    suspend fun getAllProducts(context: Context): List<ProductDTO> {

        val api = provideApi(context)
        val baseProducts = try {
            api.getProducts()
        } catch (_: Exception) {
            emptyList()
        }


        val userProducts = getUserProducts(context)


        return baseProducts + userProducts
    }
}