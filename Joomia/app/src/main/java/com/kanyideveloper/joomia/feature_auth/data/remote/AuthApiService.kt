package com.kanyideveloper.joomia.feature_auth.data.remote

import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.data.remote.request.AuthRequest
import com.kanyideveloper.joomia.feature_auth.data.remote.response.AuthResponse
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): UserResponseDto

    @PATCH("api/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body user: User
    ): UserResponseDto

    @GET("api/accounts/admin")
    suspend fun checkAdminAccount(@Query("userName") userName: String): Boolean
}