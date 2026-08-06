package com.nexus.inventory.data.dto

import com.google.gson.annotations.SerializedName
import com.nexus.inventory.domain.model.Order
import com.nexus.inventory.domain.model.OrderItem
import java.math.BigDecimal

data class OrderItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("productId") val productId: Long,
    @SerializedName("productName") val productName: String,
    @SerializedName("productSku") val productSku: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: BigDecimal,
    @SerializedName("subtotal") val subtotal: BigDecimal
)

data class OrderDto(
    @SerializedName("id") val id: Long,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("status") val status: String,
    @SerializedName("totalAmount") val totalAmount: BigDecimal,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("items") val items: List<OrderItemDto>?
)

fun OrderItemDto.toDomain(): OrderItem = OrderItem(
    id = id,
    productId = productId,
    productName = productName,
    productSku = productSku,
    quantity = quantity,
    unitPrice = unitPrice,
    subtotal = subtotal
)

fun OrderDto.toDomain(): Order = Order(
    id = id,
    customerName = customerName,
    status = status,
    totalAmount = totalAmount,
    createdAt = createdAt ?: "",
    items = items?.map { it.toDomain() } ?: emptyList()
)
