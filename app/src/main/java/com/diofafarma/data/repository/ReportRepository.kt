package com.diofafarma.data.repository

import com.diofafarma.data.model.Product
import com.diofafarma.data.model.ReportResumen
import com.diofafarma.data.model.StockBajoItem
import com.diofafarma.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportRepository {
    private val api = RetrofitClient.apiService

    suspend fun getResumen(): ReportResumen = withContext(Dispatchers.IO) {
        val response = api.getResumenReporte()
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Respuesta de reporte vacía")
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener reporte resumen")
        }
    }

    suspend fun getStockBajo(umbral: Int = 10): List<StockBajoItem> = withContext(Dispatchers.IO) {
        val response = api.getStockBajo(umbral)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener productos con stock bajo")
        }
    }

    suspend fun getProductosActivos(): List<Product> = withContext(Dispatchers.IO) {
        val response = api.getProductosActivos()
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener catálogo activo")
        }
    }
}
