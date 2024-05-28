package com.kanyideveloper.joomia.feature_products.data.remote.dto


import com.google.gson.annotations.SerializedName

data class MobileDto(
    @SerializedName("mobileType")
    val mobileType: String,
    @SerializedName("mobileDescription")
    val mobileDescription: String,
    @SerializedName("mobileID")
    val mobileID: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("mobilePrice")
    val mobilePrice: Double,
    @SerializedName("rating")
    val ratingDto: RatingDto,
    @SerializedName("mobileName")
    val mobileName: String
)