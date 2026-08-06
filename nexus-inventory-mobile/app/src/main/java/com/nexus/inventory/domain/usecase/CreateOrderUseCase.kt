package com.nexus.inventory.domain.usecase

import com.nexus.inventory.domain.model.Order
import com.nexus.inventory.domain.repository.InventoryRepository

class CreateOrderUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(customerName: String, items: Map<Long, Int>): Result<Order> {
        if (customerName.isBlank()) {
            return Result.failure(IllegalArgumentException("Customer name cannot be empty"))
        }
        if (items.isEmpty()) {
            return Result.failure(IllegalArgumentException("Order must contain at least one item"))
        }
        return repository.createOrder(customerName, items)
    }
}
