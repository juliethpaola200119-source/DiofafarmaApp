package com.diofafarma.data.model

data class Client(
    val id: Int,
    val tipoDocumento: String?,
    val documento: String,
    val nombre: String,
    val apellido: String?,
    val telefono: String?,
    val email: String?,
    val direccion: String?,
    val estado: Boolean?
) {
    val nombreCompleto: String
        get() = if (apellido.isNullOrEmpty()) nombre else "$nombre $apellido".trim()
}
data class ClientRequest(
    val tipoDocumento: String = "CC",
    val documento: String,
    val nombre: String,
    val apellido: String?,
    val telefono: String?,
    val email: String?,
    val direccion: String?,
    val estado: Boolean = true
)
