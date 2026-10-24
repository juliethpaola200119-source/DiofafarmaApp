package com.diofafarma.data.repository

import com.diofafarma.data.model.Sale
import com.diofafarma.data.model.SaleCreateRequest
import com.diofafarma.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaleRepository {
    private val api = RetrofitClient.apiService

    suspend fun getVentas(busqueda: String? = null): List<Sale> = withContext(Dispatchers.IO) {
        val response = api.getVentas(busqueda)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener ventas (${response.code()})")
        }
    }

    suspend fun getVentaPorId(id: Int): Sale = withContext(Dispatchers.IO) {
        val response = api.getVentaPorId(id)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Venta no encontrada")
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener la venta")
        }
    }

    suspend fun crearVenta(request: SaleCreateRequest): Sale = withContext(Dispatchers.IO) {
        val response = api.crearVenta(request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Error al crear la venta")
        } else {
            throw Exception(response.body()?.message ?: "Error al registrar la venta")
        }
    }

    suspend fun anularVenta(id: Int) = withContext(Dispatchers.IO) {
        val response = api.anularVenta(id)
        if (!response.isSuccessful || response.body()?.success != true) {
            throw Exception(response.body()?.message ?: "Error al anular la venta")
        }
    }
}
