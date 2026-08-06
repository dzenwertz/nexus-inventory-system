package com.nexus.inventory.ui.dashboard

import com.nexus.inventory.domain.model.DashboardMetrics

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val metrics: DashboardMetrics) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
