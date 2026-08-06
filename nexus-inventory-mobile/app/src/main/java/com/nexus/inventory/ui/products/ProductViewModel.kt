package com.nexus.inventory.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.inventory.domain.usecase.CreateOrderUseCase
import com.nexus.inventory.domain.usecase.GetProductsUseCase
import com.nexus.inventory.domain.usecase.UpdateStockUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val updateStockUseCase: UpdateStockUseCase,
    private val createOrderUseCase: CreateOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts(query: String? = null) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            getProductsUseCase(query)
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { exception ->
                    _uiState.value = ProductUiState.Error(
                        exception.localizedMessage ?: "Error al cargar la lista de productos"
                    )
                }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        loadProducts(newQuery)
    }

    fun updateProductStock(productId: Long, newStock: Int, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            updateStockUseCase(productId, newStock)
                .onSuccess {
                    loadProducts(_searchQuery.value)
                    onResult(true, null)
                }
                .onFailure { ex ->
                    onResult(false, ex.localizedMessage ?: "Error al actualizar stock")
                }
        }
    }

    fun createNewOrder(customerName: String, items: Map<Long, Int>, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            createOrderUseCase(customerName, items)
                .onSuccess {
                    loadProducts(_searchQuery.value)
                    onResult(true, null)
                }
                .onFailure { ex ->
                    onResult(false, ex.localizedMessage ?: "Error al crear la orden")
                }
        }
    }
}
