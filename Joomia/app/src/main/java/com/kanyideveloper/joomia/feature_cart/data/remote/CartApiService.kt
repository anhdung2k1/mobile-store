package com.kanyideveloper.joomia.feature_cart.data.remote

import com.kanyideveloper.joomia.feature_cart.data.remote.dto.UserCartResponseDto
import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CartApiService {
    @GET("api/carts/user/{id}")
    suspend fun cartItems(
        @Path("id") userId: Int
    ): UserCartResponseDto

    @GET("api/mobiles/{id}")
    suspend fun mobile(
        @Path("id") id: Int
    ): MobileDto
}