package com.kanyideveloper.joomia.feature_cart.data.remote.mapper

import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.PayPalAccessTokenResponse
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal.PayPalOrderDto
import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalAccessToken
import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalOrder

fun PayPalAccessTokenResponse.toDomain(): PayPalAccessToken {
    return PayPalAccessToken(
        token = this.accessToken,
        tokenType = this.tokenType,
        expiresIn = this.expiresIn,
        scope = this.scope
    )
}

fun PayPalOrderDto.toDomain(): PayPalOrder {
    val approvalLink = this.links.find { it.rel == "approve" }
    return PayPalOrder(
        id = this.id,
        status = this.status,
        approvalUrl = approvalLink?.href.orEmpty()
    )
}