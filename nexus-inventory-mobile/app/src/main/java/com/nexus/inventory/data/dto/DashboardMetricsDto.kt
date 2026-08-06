package com.nexus.inventory.data.dto

import com.google.gson.annotations.SerializedName
import com.nexus.inventory.domain.model.DashboardMetrics

data class DashboardMetricsDto(
    @SerializedName("totalProducts") val totalProducts: Long,
    @SerializedName("totalStockUnits") val totalStockUnits: Long,
    @SerializedName("activeOrders") val activeOrders: Long,
    @SerializedName("lowStockCount") val lowStockCount: Long,
    @SerializedName("lowStockAlerts") val lowStockAlerts: List<ProductDto>?
)

fun DashboardMetricsDto.toDomain(): DashboardMetrics = DashboardMetrics(
    totalProducts = totalProducts,
    totalStockUnits = totalStockUnits,
    activeOrders = activeOrders,
    lowStockCount = lowStockCount,
    lowStockAlerts = lowStockAlerts?.map { it.toDomain() } ?: emptyList()
)
