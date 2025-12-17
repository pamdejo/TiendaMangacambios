package com.example.tiendamanga


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Query
import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path


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

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Query("id") id: Int,
        @Body body: ProductCreateDTO
    ): ProductDTO

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(
        @Query("id") id: Int
    )
}


fun provideApi(context: Context): ApiService {

    val baseUrl = "http://10.0.2.2/tienda_api/"  // ✅ NO localhost

    val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}