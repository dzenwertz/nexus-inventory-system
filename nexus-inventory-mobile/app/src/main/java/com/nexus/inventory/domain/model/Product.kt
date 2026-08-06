package com.nexus.inventory.domain.model

import java.math.BigDecimal

data class Product(
    val id: Long,
    val sku: String,
    val name: String,
    val price: BigDecimal,
    val stock: Int,
    val minStockLevel: Int,
    val categoryId: Long?,
    val categoryName: String,
    val stockStatus: StockStatus
)
