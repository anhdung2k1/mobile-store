package com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal

import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalOrder
import com.kanyideveloper.joomia.feature_cart.domain.repository.PayPalRepository

class CreateOrderUseCase(private val repository: PayPalRepository) {
    suspend fun execute(accessToken: String, amount: String, currency: String): PayPalOrder {
        return repository.createOrder(accessToken, amount, currency)
    }
}