package com.nexus.inventory.domain.usecase

import com.nexus.inventory.domain.model.DashboardMetrics
import com.nexus.inventory.domain.repository.InventoryRepository

class GetDashboardUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(): Result<DashboardMetrics> {
        return repository.getDashboardMetrics()
    }
}
