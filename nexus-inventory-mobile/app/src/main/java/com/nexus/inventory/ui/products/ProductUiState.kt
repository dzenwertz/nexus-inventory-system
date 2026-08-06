package com.nexus.inventory.ui.products

import com.nexus.inventory.domain.model.Product

sealed interface ProductUiState {
    data object Loading : ProductUiState
    data class Success(val products: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
}
