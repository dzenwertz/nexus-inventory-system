package com.nexus.inventory.domain.model

data class DashboardMetrics(
    val totalProducts: Long,
    val totalStockUnits: Long,
    val activeOrders: Long,
    val lowStockCount: Long,
    val lowStockAlerts: List<Product>
)
