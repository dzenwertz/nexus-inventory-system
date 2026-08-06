package com.nexus.inventory.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.inventory.domain.usecase.GetDashboardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardUseCase: GetDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            getDashboardUseCase()
                .onSuccess { metrics ->
                    _uiState.value = DashboardUiState.Success(metrics)
                }
                .onFailure { exception ->
                    _uiState.value = DashboardUiState.Error(
                        exception.localizedMessage ?: "Error al cargar las métricas del Dashboard"
                    )
                }
        }
    }
}
