package com.example.tiendamanga

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PREFS_NAME = "mangazone_admin"
private const val KEY_EXTRA_PRODUCTS = "extra_products"

private val gson = Gson()


fun saveExtraProduct(context: Context, product: ProductDTO) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val currentJson = prefs.getString(KEY_EXTRA_PRODUCTS, "[]")
    val type = object : TypeToken<MutableList<ProductDTO>>() {}.type
    val list: MutableList<ProductDTO> = gson.fromJson(currentJson, type)

    list.add(product)

    prefs.edit()
        .putString(KEY_EXTRA_PRODUCTS, gson.toJson(list))
        .apply()
}


fun loadMergedProducts(context: Context, base: List<ProductDTO>): List<ProductDTO> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val json = prefs.getString(KEY_EXTRA_PRODUCTS, "[]")
    val type = object : TypeToken<List<ProductDTO>>() {}.type
    val extras: List<ProductDTO> = gson.fromJson(json, type)


    return base + extras
}