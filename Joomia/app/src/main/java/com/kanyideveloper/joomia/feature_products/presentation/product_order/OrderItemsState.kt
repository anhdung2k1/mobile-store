package com.kanyideveloper.joomia.feature_products.presentation.product_order

import com.kanyideveloper.joomia.feature_products.domain.model.Order

data class OrderItemsState (
    val orderItems: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)