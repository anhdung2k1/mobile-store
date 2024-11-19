package com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal

import com.google.gson.annotations.SerializedName

data class PayPalLinkDto (
    @SerializedName("href")
    val href: String,

    @SerializedName("rel")
    val rel: String,

    @SerializedName("method")
    val method: String
)