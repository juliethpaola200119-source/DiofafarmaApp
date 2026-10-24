package com.diofafarma.data.repository

import com.diofafarma.data.model.LoginRequest
import com.diofafarma.data.model.LoginResponse
import com.diofafarma.data.remote.ApiService
import com.diofafarma.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val api = RetrofitClient.instance.create(ApiService::class.java)

    suspend fun login(usuario: String, password: String): LoginResponse = withContext(Dispatchers.IO) {
        val response = api.login(LoginRequest(usuario, password))
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.success) {
                body
            } else {
                throw Exception(body?.message ?: "Error en la autenticación")
            }
        } else {
            throw Exception("Error ${response.code()}: Credenciales inválidas")
        }
    }
}
