package com.example.tiendamanga

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 🟣 DTO que coincide con tu products.json
data class ProductDTO(
    val id: Int,
    val nombre: String,
    val precio: Int,
    val imagen: String,
    val stock: Int,
    val destacado: Boolean,
    val categoria: String? = null
)

data class ProductCreateDTO(
    val nombre: String,
    val precio: Int,
    val imagen: String,
    val stock: Int,
    val destacado: Boolean,
    val categoria: String
)

interface ApiService {

    @GET("api/products")
    suspend fun getProducts(): List<ProductDTO>

    @POST("api/products")
    suspend fun createProduct(@Body body: ProductCreateDTO): ProductDTO
}

// 🟣 Retrofit usando tu AssetsInterceptor
fun provideApi(context: Context): ApiService {
    val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(AssetsInterceptor(context))
        .build()

    return Retrofit.Builder()
        .baseUrl("http://localhost/")  // lo usamos sólo como base falsa para Retrofit
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}