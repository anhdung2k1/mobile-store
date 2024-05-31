package com.kanyideveloper.joomia.feature_products.data.remote.dto


import com.google.gson.annotations.SerializedName

data class MobileDto(
    @SerializedName("mobileID")
    val mobileID: Int,
    @SerializedName("mobileType")
    val mobileType: String,
    @SerializedName("mobileDescription")
    val mobileDescription: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("mobilePrice")
    val mobilePrice: Double,
    @SerializedName("mobileQuantity")
    val mobileQuantity: Int,
    @SerializedName("rating")
    val ratingDto: RatingDto,
    @SerializedName("mobileName")
    val mobileName: String,
    @SerializedName("mobileModel")
    val mobileModel: String
)