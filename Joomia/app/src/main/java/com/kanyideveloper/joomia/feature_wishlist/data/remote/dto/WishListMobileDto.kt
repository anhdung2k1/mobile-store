package com.kanyideveloper.joomia.feature_wishlist.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WishListMobileDto(
    @SerializedName("mobileID")
    val mobileID: Int,
    @SerializedName("mobileQuantity")
    val mobileQuantity: Int
)