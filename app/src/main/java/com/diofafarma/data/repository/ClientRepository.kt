package com.diofafarma.data.repository

import com.diofafarma.data.model.Client
import com.diofafarma.data.model.ClientRequest
import com.diofafarma.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientRepository {
    private val api = RetrofitClient.apiService

    suspend fun getClientes(busqueda: String? = null): List<Client> = withContext(Dispatchers.IO) {
        val response = api.getClientes(busqueda)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener clientes (${response.code()})")
        }
    }

    suspend fun crearCliente(request: ClientRequest): Client = withContext(Dispatchers.IO) {
        val response = api.crearCliente(request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Respuesta vacía al crear cliente")
        } else {
            throw Exception(response.body()?.message ?: "Error al crear cliente")
        }
    }

    suspend fun actualizarCliente(id: Int, request: ClientRequest): Client = withContext(Dispatchers.IO) {
        val response = api.actualizarCliente(id, request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Respuesta vacía al actualizar cliente")
        } else {
            throw Exception(response.body()?.message ?: "Error al actualizar cliente")
        }
    }

    suspend fun eliminarCliente(id: Int) = withContext(Dispatchers.IO) {
        val response = api.eliminarCliente(id)
        if (!response.isSuccessful || response.body()?.success != true) {
            throw Exception(response.body()?.message ?: "Error al eliminar cliente")
        }
    }
}
