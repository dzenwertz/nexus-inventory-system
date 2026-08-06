package com.nexus.inventory.data.dto

import com.google.gson.annotations.SerializedName
import com.nexus.inventory.domain.model.Product
import com.nexus.inventory.domain.model.StockStatus
import java.math.BigDecimal

data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("sku") val sku: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: BigDecimal,
    @SerializedName("stock") val stock: Int,
    @SerializedName("minStockLevel") val minStockLevel: Int,
    @SerializedName("categoryId") val categoryId: Long?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("stockStatus") val stockStatus: String?
)

fun ProductDto.toDomain(): Product {
    val status = when (stockStatus) {
        "OUT_OF_STOCK" -> StockStatus.OUT_OF_STOCK
        "LOW" -> StockStatus.LOW
        else -> if (stock == 0) StockStatus.OUT_OF_STOCK else if (stock <= minStockLevel) StockStatus.LOW else StockStatus.SUFFICIENT
    }

    return Product(
        id = id,
        sku = sku,
        name = name,
        price = price,
        stock = stock,
        minStockLevel = minStockLevel,
        categoryId = categoryId,
        categoryName = categoryName ?: "Uncategorized",
        stockStatus = status
    )
}
