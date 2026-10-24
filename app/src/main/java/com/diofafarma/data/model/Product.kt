package com.diofafarma.data.model

data class Product(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val laboratorio: String?,
    val precioCompra: Double,
    val precioVenta: Double,
    val stock: Int,
    val estado: Boolean
)

data class ProductRequest(
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val laboratorio: String?,
    val precioCompra: Double,
    val precioVenta: Double,
    val stock: Int,
    val estado: Boolean = true
)
