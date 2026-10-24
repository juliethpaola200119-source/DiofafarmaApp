package com.diofafarma.data.repository

import com.diofafarma.data.model.ApiResponse
import com.diofafarma.data.model.User
import com.diofafarma.data.model.UserRequest
import com.diofafarma.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val api = RetrofitClient.apiService

    suspend fun getUsuarios(): List<User> = withContext(Dispatchers.IO) {
        val response = api.getUsuarios()
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception(response.body()?.message ?: "Error al obtener usuarios (${response.code()})")
        }
    }

    suspend fun crearUsuario(request: UserRequest): User = withContext(Dispatchers.IO) {
        val response = api.crearUsuario(request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Respuesta vacía al crear usuario")
        } else {
            throw Exception(response.body()?.message ?: "Error al crear usuario")
        }
    }

    suspend fun actualizarUsuario(id: Int, request: UserRequest): User = withContext(Dispatchers.IO) {
        val response = api.actualizarUsuario(id, request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data ?: throw Exception("Respuesta vacía al actualizar usuario")
        } else {
            throw Exception(response.body()?.message ?: "Error al actualizar usuario")
        }
    }

    suspend fun eliminarUsuario(id: Int) = withContext(Dispatchers.IO) {
        val response = api.eliminarUsuario(id)
        if (!response.isSuccessful || response.body()?.success != true) {
            throw Exception(response.body()?.message ?: "Error al eliminar usuario")
        }
    }
}
