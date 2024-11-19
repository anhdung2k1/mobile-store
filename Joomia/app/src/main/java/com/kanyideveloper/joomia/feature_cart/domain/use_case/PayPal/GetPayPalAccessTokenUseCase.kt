package com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal

import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalAccessToken
import com.kanyideveloper.joomia.feature_cart.domain.repository.PayPalRepository

class GetPayPalAccessTokenUseCase(
    private val repository: PayPalRepository
) {
    suspend fun execute() : PayPalAccessToken {
        return repository.getAccessToken()
    }
}