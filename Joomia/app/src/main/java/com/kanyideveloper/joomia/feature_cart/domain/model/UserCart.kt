package com.kanyideveloper.joomia.feature_cart.domain.model

data class UserCart(
    val date: String,
    val cartID: Int,
    val cartMobiles: List<CartMobile>,
    val userId: Int,
)
