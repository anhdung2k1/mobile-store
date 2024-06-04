package com.kanyideveloper.joomia.feature_cart.presentation.checkout

import com.kanyideveloper.joomia.feature_cart.domain.model.Payment

data class PaymentState (
    val paymentItems: List<Payment>,
    val isLoading: Boolean = false,
    val error: String? = null
)