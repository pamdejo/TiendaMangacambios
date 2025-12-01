package com.example.tiendamanga.com.example.tiendamanga


data class ProductDTO(
    val id: Int,
    val nombre: String,
    val precio: Int,
    val imagen: String,
    val stock: Int,
    val destacado: Boolean,
    val categoria: String = "General" // 👈 importante: String normal, con valor por defecto
)