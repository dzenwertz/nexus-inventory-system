package com.nexus.inventory.domain.repository

import com.nexus.inventory.domain.model.DashboardMetrics
import com.nexus.inventory.domain.model.Order
import com.nexus.inventory.domain.model.Product

interface InventoryRepository {
    suspend fun getProducts(searchQuery: String? = null): Result<List<Product>>
    suspend fun getProductById(id: Long): Result<Product>
    suspend fun getLowStockProducts(): Result<List<Product>>
    suspend fun updateStock(productId: Long, newStock: Int): Result<Product>
    suspend fun createOrder(customerName: String, items: Map<Long, Int>): Result<Order>
    suspend fun getDashboardMetrics(): Result<DashboardMetrics>
    suspend fun getOrders(): Result<List<Order>>
}
