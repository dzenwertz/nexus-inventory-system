package com.nexus.inventory.domain.usecase

import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.domain.repository.InventoryRepository

class UpdateStockUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(productId: Long, newStock: Int): Result<Product> {
        if (newStock < 0) {
            return Result.failure(IllegalArgumentException("Stock level cannot be negative"))
        }
        return repository.updateStock(productId, newStock)
    }
}
