package com.kanyideveloper.joomia.feature_auth.data.remote

import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.data.remote.request.AuthRequest
import com.kanyideveloper.joomia.feature_auth.data.remote.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/accounts/signin")
    suspend fun loginUser(
        @Body authRequest: AuthRequest
    ): AuthResponse

    @POST("api/accounts/signup")
    suspend fun registerUser(
        @Body registerRequest: AuthRequest
    ) : AuthResponse

    @GET("api/users")
    suspend fun getAllUsers(): List<UserResponseDto>
}