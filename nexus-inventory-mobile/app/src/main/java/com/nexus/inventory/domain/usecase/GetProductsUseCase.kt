package com.nexus.inventory.domain.usecase

import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.domain.repository.InventoryRepository

class GetProductsUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(searchQuery: String? = null): Result<List<Product>> {
        return repository.getProducts(searchQuery)
    }
}
