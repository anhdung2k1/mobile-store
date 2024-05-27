package com.kanyideveloper.joomia.feature_cart.data.remote.dto


import com.google.gson.annotations.SerializedName

data class UserCartResponseDto(
    @SerializedName("date")
    val date: String,
    @SerializedName("cartID")
    val cartID: Int,
    @SerializedName("mobiles")
    val cartMobileDtos: List<CartMobileDto>,
    @SerializedName("userId")
    val userId: Int,
)