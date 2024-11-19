package com.kanyideveloper.joomia.feature_wishlist.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserWishListResponseDto(
    @SerializedName("wishListID")
    val wishListID: Int,
    @SerializedName("mobiles")
    val wishListMobileDtos: List<WishListMobileDto>,
    @SerializedName("userId")
    val userId: Int,
)