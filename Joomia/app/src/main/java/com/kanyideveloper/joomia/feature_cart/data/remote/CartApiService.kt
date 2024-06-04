package com.kanyideveloper.joomia.feature_cart.data.remote

import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PaymentDto
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.UserCartResponseDto
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_cart.domain.model.Payment
import com.kanyideveloper.joomia.feature_cart.domain.model.Transaction
import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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

    @POST("api/carts/user/{id}")
    suspend fun createCartItem(@Path("id") id: Int, @Body cartMobile: CartMobile): UserCartResponseDto

    @PATCH("api/carts/user/{id}")
    suspend fun updateCart(@Path("id") id: Int, @Body cartMobile: CartMobile): UserCartResponseDto

    // Payment API
    @GET("api/payments")
    suspend fun getAllPayments(): List<PaymentDto>

    // Transaction API
    @POST("api/transactions/{userId}")
    suspend fun createTransaction(@Path("userId") userId: Int, @Body transaction: Transaction): Boolean

}