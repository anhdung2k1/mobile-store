package com.kanyideveloper.joomia.feature_products.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.TransactionDto

data class OrderDto (
    @SerializedName("orderID")
    val orderID: Int,
    @SerializedName("userID")
    val userID: Int,
    @SerializedName("transactionID")
    val transactionID: Int,
    @SerializedName("orderDate")
    val orderDate: String,
    @SerializedName("orderStatus")
    val orderStatus: String,
    @SerializedName("totalAmount")
    val totalAmount: Double
)