package com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal

import com.google.gson.annotations.SerializedName

data class PayPalAccessTokenResponse (
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("expires_in")
    val expiresIn: Long,

    @SerializedName("scope")
    val scope: String
)