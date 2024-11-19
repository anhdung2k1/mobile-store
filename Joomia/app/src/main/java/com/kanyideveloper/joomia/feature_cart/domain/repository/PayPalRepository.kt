package com.kanyideveloper.joomia.feature_cart.domain.repository

import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalAccessToken
import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalOrder

interface PayPalRepository {
    suspend fun getAccessToken(): PayPalAccessToken
    suspend fun createOrder(accessToken: String, amount: String, currency: String): PayPalOrder
    suspend fun captureOrder(accessToken: String, orderId: String): PayPalOrder
}