package com.kanyideveloper.joomia.feature_cart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentDto (
    @SerializedName("paymentID")
    val paymentID: Int,
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)