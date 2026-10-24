package com.diofafarma.data.model

data class User(
    val id: Int,
    val usuario: String,
    val email: String?,
    val rol: String?,
    val estado: Int?
)

data class LoginRequest(
    val usuario: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthData?
)

data class AuthData(
    val id: Int,
    val usuario: String,
    val email: String?,
    val rol: String?,
    val token: String?
)

data class UserRequest(
    val usuario: String,
    val email: String?,
    val password: String,
    val rol: String?
)
