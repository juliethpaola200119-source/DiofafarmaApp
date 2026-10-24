package com.diofafarma.data.model

data class ReportResumen(
    val totalProductos: Int,
    val productosActivos: Int,
    val productosStockBajo: Int,
    val totalUsuarios: Int
)

data class StockBajoItem(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val laboratorio: String?,
    val stock: Int,
    val precio_venta: Double
)
