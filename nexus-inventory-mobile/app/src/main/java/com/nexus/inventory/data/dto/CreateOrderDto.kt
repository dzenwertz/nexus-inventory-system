package com.nexus.inventory.data.dto

import com.google.gson.annotations.SerializedName

data class OrderItemRequestDto(
    @SerializedName("productId") val productId: Long,
    @SerializedName("quantity") val quantity: Int
)

data class CreateOrderRequestDto(
    @SerializedName("customerName") val customerName: String,
    @SerializedName("items") val items: List<OrderItemRequestDto>
)

data class StockUpdateDto(
    @SerializedName("stock") val stock: Int
)
