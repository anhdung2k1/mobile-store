package com.kanyideveloper.joomia.feature_cart.domain.model.PayPal

data class PayPalAccessToken (
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
    val scope: String
)