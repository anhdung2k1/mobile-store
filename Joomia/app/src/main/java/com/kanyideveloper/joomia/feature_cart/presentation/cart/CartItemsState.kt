package com.kanyideveloper.joomia.feature_cart.presentation.cart

import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile

data class CartItemsState(
    val cartItems: List<CartMobile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)