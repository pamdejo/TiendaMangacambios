package com.example.tiendamanga.com.example.tiendamanga.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String? = null
)