package com.kanyideveloper.joomia.feature_auth.data.dto


import com.google.gson.annotations.SerializedName
import java.util.Date

data class UserResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("birthDay")
    val birthDay: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)