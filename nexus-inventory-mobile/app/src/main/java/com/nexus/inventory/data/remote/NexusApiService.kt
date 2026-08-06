package com.nexus.inventory.data.remote

import com.nexus.inventory.data.dto.*
import retrofit2.http.*

interface NexusApiService {

    @GET("api/products")
    suspend fun getProducts(@Query("search") search: String? = null): List<ProductDto>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): ProductDto

    @GET("api/products/low-stock")
    suspend fun getLowStockProducts(): List<ProductDto>

    @PUT("api/products/{id}/stock")
    suspend fun updateStock(
        @Path("id") id: Long,
        @Body stockUpdate: StockUpdateDto
    ): ProductDto

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequestDto): OrderDto

    @GET("api/orders")
    suspend fun getOrders(@Query("status") status: String? = null): List<OrderDto>

    @GET("api/dashboard")
    suspend fun getDashboardMetrics(): DashboardMetricsDto
}
