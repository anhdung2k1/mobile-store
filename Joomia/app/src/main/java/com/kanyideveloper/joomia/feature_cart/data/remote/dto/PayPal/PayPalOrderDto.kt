package com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal

import com.google.gson.annotations.SerializedName

data class PayPalOrderDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("links")
    val links: List<PayPalLinkDto>
)
