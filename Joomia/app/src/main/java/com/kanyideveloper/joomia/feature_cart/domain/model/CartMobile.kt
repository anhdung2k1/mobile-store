package com.kanyideveloper.joomia.feature_cart.domain.model

data class CartMobile(
    val mobileID: Int,
    val mobileName: String,
    val mobilePrice: Double,
    val mobileQuantity: Int,
    val imageUrl: String
)
