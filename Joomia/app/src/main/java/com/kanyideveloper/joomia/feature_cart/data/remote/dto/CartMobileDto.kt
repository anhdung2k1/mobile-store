package com.kanyideveloper.joomia.feature_cart.data.remote.dto


import com.google.gson.annotations.SerializedName

data class CartMobileDto(
    @SerializedName("mobileID")
    val mobileID: Int,
    @SerializedName("mobileQuantity")
    val mobileQuantity: Int
)