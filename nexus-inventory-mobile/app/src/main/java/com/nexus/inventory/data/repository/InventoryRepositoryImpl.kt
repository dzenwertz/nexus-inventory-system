package com.nexus.inventory.data.repository

import com.nexus.inventory.data.dto.*
import com.nexus.inventory.data.remote.NexusApiService
import com.nexus.inventory.domain.model.DashboardMetrics
import com.nexus.inventory.domain.model.Order
import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.domain.repository.InventoryRepository

class InventoryRepositoryImpl(
    private val apiService: NexusApiService
) : InventoryRepository {

    override suspend fun getProducts(searchQuery: String?): Result<List<Product>> {
        return runCatching {
            apiService.getProducts(searchQuery).map { it.toDomain() }
        }
    }

    override suspend fun getProductById(id: Long): Result<Product> {
        return runCatching {
            apiService.getProductById(id).toDomain()
        }
    }

    override suspend fun getLowStockProducts(): Result<List<Product>> {
        return runCatching {
            apiService.getLowStockProducts().map { it.toDomain() }
        }
    }

    override suspend fun updateStock(productId: Long, newStock: Int): Result<Product> {
        return runCatching {
            apiService.updateStock(productId, StockUpdateDto(newStock)).toDomain()
        }
    }

    override suspend fun createOrder(customerName: String, items: Map<Long, Int>): Result<Order> {
        return runCatching {
            val itemDtos = items.map { (prodId, qty) -> OrderItemRequestDto(prodId, qty) }
            val request = CreateOrderRequestDto(customerName, itemDtos)
            apiService.createOrder(request).toDomain()
        }
    }

    override suspend fun getDashboardMetrics(): Result<DashboardMetrics> {
        return runCatching {
            apiService.getDashboardMetrics().toDomain()
        }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        return runCatching {
            apiService.getOrders().map { it.toDomain() }
        }
    }
}
