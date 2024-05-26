package com.kanyideveloper.joomia.feature_auth.data.remote.request


import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("password")
    val password: String,
    @SerializedName("userName")
    val userName: String
)