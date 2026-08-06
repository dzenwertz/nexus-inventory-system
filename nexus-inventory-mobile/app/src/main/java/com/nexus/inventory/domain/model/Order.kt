package com.nexus.inventory.domain.model

import java.math.BigDecimal

data class OrderItem(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productSku: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val subtotal: BigDecimal
)

data class Order(
    val id: Long,
    val customerName: String,
    val status: String,
    val totalAmount: BigDecimal,
    val createdAt: String,
    val items: List<OrderItem>
)
