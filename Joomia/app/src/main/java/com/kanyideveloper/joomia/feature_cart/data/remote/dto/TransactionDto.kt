package com.kanyideveloper.joomia.feature_cart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TransactionDto (
    @SerializedName("transactionID")
    val transactionID: Int,
    @SerializedName("transactionType")
    val transactionType: String,
    @SerializedName("shippingAddress")
    val shippingAddress: String,
    @SerializedName("billingPayment")
    val billingPayment: Double,
    @SerializedName("payment")
    val payment: PaymentDto
)