package com.kanyideveloper.joomia.feature_cart.data.repository

import com.kanyideveloper.joomia.feature_cart.data.remote.PayPalApiService
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.Amount
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.OrderRequest
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.PurchaseUnit
import com.kanyideveloper.joomia.feature_cart.data.remote.mapper.toDomain
import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalAccessToken
import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalOrder
import com.kanyideveloper.joomia.feature_cart.domain.repository.PayPalRepository
import okhttp3.Credentials

class PayPalRepositoryImpl(
    private val apiService: PayPalApiService,
    private val clientId: String,
    private val secret: String
) : PayPalRepository {
    override suspend fun getAccessToken(): PayPalAccessToken {
        val credentials = Credentials.basic(clientId, secret)
        val response = apiService.getAccessToken(credentials)
        return response.toDomain()
    }

    override suspend fun createOrder(accessToken: String, amount: String, currency: String): PayPalOrder {
        val orderRequest = OrderRequest(
            intent = "CAPTURE",
            purchaseUnits = listOf(
                PurchaseUnit(
                    amount = Amount(
                        currencyCode = currency,
                        value = amount
                    )
                )
            )
        )
        val response = apiService.createOrder("Bearer $accessToken", orderRequest)
        return response.toDomain()
    }

    override suspend fun captureOrder(accessToken: String, orderId: String): PayPalOrder {
        val response = apiService.captureOrder("Bearer $accessToken", orderId)
        return response.toDomain()
    }
}
