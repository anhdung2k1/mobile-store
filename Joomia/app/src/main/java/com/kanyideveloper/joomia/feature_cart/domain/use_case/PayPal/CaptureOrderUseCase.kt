package com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal

import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalOrder
import com.kanyideveloper.joomia.feature_cart.domain.repository.PayPalRepository

class CaptureOrderUseCase(private val repository: PayPalRepository) {
    suspend fun execute(accessToken: String, orderId: String): PayPalOrder {
        return repository.captureOrder(accessToken, orderId)
    }
}