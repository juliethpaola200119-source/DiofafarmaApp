package com.diofafarma.data.repository

import com.diofafarma.data.model.Product
import com.diofafarma.data.model.ProductRequest
import com.diofafarma.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository {
    private val api = RetrofitClient.apiService

    suspend fun getProductos(): List<Product> = withContext(Dispatchers.IO) {
        val response = api.getProductos()
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener productos (${response.code()})")
        }
    }

    suspend fun crearProducto(request: ProductRequest): Product = withContext(Dispatchers.IO) {
        val response = api.crearProducto(request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Respuesta vacía al crear producto")
        } else {
            throw Exception(response.body()?.message ?: "Error al crear producto")
        }
    }

    suspend fun actualizarProducto(id: Int, request: ProductRequest): Product = withContext(Dispatchers.IO) {
        val response = api.actualizarProducto(id, request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Respuesta vacía al actualizar producto")
        } else {
            throw Exception(response.body()?.message ?: "Error al actualizar producto")
        }
    }

    suspend fun eliminarProducto(id: Int) = withContext(Dispatchers.IO) {
        val response = api.eliminarProducto(id)
        if (!response.isSuccessful || response.body()?.success != true) {
            throw Exception(response.body()?.message ?: "Error al eliminar producto")
        }
    }
}
