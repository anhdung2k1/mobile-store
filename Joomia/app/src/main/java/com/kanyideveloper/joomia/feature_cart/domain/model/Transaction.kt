package com.kanyideveloper.joomia.feature_cart.domain.model

data class Transaction (
    val transactionID: Int,
    val transactionType: String,
    val shippingAddress: String,
    val billingPayment: Double,
    val payment: Payment,
)