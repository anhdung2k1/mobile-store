package com.kanyideveloper.joomia.feature_auth.data.remote.response


import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String
)