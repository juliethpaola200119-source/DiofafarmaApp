package com.diofafarma.data.model
data class Sale(
    val id: Int,
    val codigo: String,
    val clienteId: Int?,
    val clienteNombre: String?,
    val clienteDoc: String?,
    val usuarioId: Int?,
    val tipoPago: String,
    val subtotal: Double,
    val iva: Double,
    val total: Double,
    val estado: String,
    val observaciones: String?,
    val fecha: String?,
    val items: List<SaleItem>?
)
data class SaleItem(
    val id: Int?,
    val ventaId: Int?,
    val productoId: Int,
    val codigoProducto: String?,
    val nombreProducto: String?,
    val cantidad: Int,
    val precioUnitario: Double,
    val iva: Double?,
    val subtotal: Double?,
    val total: Double
)

data class SaleCreateRequest(
    val clienteId: Int?,
    val tipoPago: String = "Efectivo",
    val observaciones: String = "",
    val items: List<SaleItemInput>
)

data class SaleItemInput(
    val productoId: Int,
    val cantidad: Int
)
