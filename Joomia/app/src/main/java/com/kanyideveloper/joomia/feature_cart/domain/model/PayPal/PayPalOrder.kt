package com.kanyideveloper.joomia.feature_cart.domain.model.PayPal

data class PayPalOrder (
    val id: String,
    val status: String,
    val approvalUrl: String
)