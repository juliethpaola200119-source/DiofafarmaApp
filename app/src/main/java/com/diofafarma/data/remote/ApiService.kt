package com.diofafarma.data.remote

import com.diofafarma.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== 1. Autenticación ====================
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ==================== 2. Gestión de Usuarios ====================
    @GET("usuarios")
    suspend fun getUsuarios(): Response<ApiResponse<List<User>>>

    @GET("usuarios/{id}")
    suspend fun getUsuarioPorId(@Path("id") id: Int): Response<ApiResponse<User>>

    @POST("usuarios")
    suspend fun crearUsuario(@Body request: UserRequest): Response<ApiResponse<User>>

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Int, @Body request: UserRequest): Response<ApiResponse<User>>

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ==================== 3. Productos / Inventario ====================
    @GET("productos")
    suspend fun getProductos(): Response<ApiResponse<List<Product>>>

    @GET("productos/{id}")
    suspend fun getProductoPorId(@Path("id") id: Int): Response<ApiResponse<Product>>

    @POST("productos")
    suspend fun crearProducto(@Body request: ProductRequest): Response<ApiResponse<Product>>

    @PUT("productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Int, @Body request: ProductRequest): Response<ApiResponse<Product>>

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ==================== 4. Clientes ====================
    @GET("clientes")
    suspend fun getClientes(@Query("busqueda") busqueda: String? = null): Response<ApiResponse<List<Client>>>

    @GET("clientes/{id}")
    suspend fun getClientePorId(@Path("id") id: Int): Response<ApiResponse<Client>>

    @POST("clientes")
    suspend fun crearCliente(@Body request: ClientRequest): Response<ApiResponse<Client>>

    @PUT("clientes/{id}")
    suspend fun actualizarCliente(@Path("id") id: Int, @Body request: ClientRequest): Response<ApiResponse<Client>>

    @DELETE("clientes/{id}")
    suspend fun eliminarCliente(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ==================== 5 & 6. Ventas y Detalles de Ventas ====================
    @GET("ventas")
    suspend fun getVentas(@Query("busqueda") busqueda: String? = null): Response<ApiResponse<List<Sale>>>

    @GET("ventas/{id}")
    suspend fun getVentaPorId(@Path("id") id: Int): Response<ApiResponse<Sale>>

    @GET("ventas/codigo/{codigo}")
    suspend fun getVentaPorCodigo(@Path("codigo") codigo: String): Response<ApiResponse<Sale>>

    @POST("ventas")
    suspend fun crearVenta(@Body request: SaleCreateRequest): Response<ApiResponse<Sale>>

    @PUT("ventas/{id}/anular")
    suspend fun anularVenta(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @DELETE("ventas/{id}")
    suspend fun eliminarVenta(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ==================== 7. Reportes ====================
    @GET("reportes/resumen")
    suspend fun getResumenReporte(): Response<ApiResponse<ReportResumen>>

    @GET("reportes/stock-bajo")
    suspend fun getStockBajo(@Query("umbral") umbral: Int = 10): Response<ApiResponse<List<StockBajoItem>>>

    @GET("reportes/productos-activos")
    suspend fun getProductosActivos(): Response<ApiResponse<List<Product>>>
}
