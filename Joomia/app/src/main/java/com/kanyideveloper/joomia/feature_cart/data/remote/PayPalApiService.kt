package com.kanyideveloper.joomia.feature_cart.data.remote

import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.OrderRequest
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.PayPalAccessTokenResponse
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.PayPalOrderDto
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PayPalApiService {
    @POST("v1/oauth2/token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Header("Authorization") auth: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): PayPalAccessTokenResponse

    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Header("Authorization") auth: String,
        @Body order: OrderRequest
    ): PayPalOrderDto

    @POST("v2/checkout/orders/{order_id}/capture")
    suspend fun captureOrder(
        @Header("Authorization") auth: String,
        @Path("order_id") orderId: String
    ): PayPalOrderDto
}