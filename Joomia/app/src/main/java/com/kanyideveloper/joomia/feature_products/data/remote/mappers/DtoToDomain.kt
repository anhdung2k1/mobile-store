package com.kanyideveloper.joomia.feature_products.data.remote.mappers

import com.kanyideveloper.joomia.feature_cart.data.remote.dto.PaymentDto
import com.kanyideveloper.joomia.feature_cart.data.remote.dto.TransactionDto
import com.kanyideveloper.joomia.feature_cart.domain.model.Payment
import com.kanyideveloper.joomia.feature_cart.domain.model.Transaction
import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import com.kanyideveloper.joomia.feature_products.data.remote.dto.OrderDto
import com.kanyideveloper.joomia.feature_products.data.remote.dto.RatingDto
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Order
import com.kanyideveloper.joomia.feature_products.domain.model.Rating

internal fun MobileDto.toDomain(): Mobile {
    return Mobile(
        mobileID = mobileID,
        mobileType = mobileType,
        mobileDescription = mobileDescription,
        imageUrl = imageUrl,
        mobilePrice = mobilePrice,
        mobileQuantity = mobileQuantity,
        rating = ratingDto.toDomain(),
        mobileName = mobileName,
        mobileModel = mobileModel
    )
}

internal fun RatingDto.toDomain(): Rating {
    return Rating(
        count = count,
        rate = rate
    )
}

internal fun PaymentDto.toDomain(): Payment {
    return Payment(
        paymentID = paymentID,
        paymentMethod = paymentMethod,
        imageUrl = imageUrl
    )
}

internal fun Payment.toDto() : PaymentDto {
    return PaymentDto(
        paymentID = paymentID,
        paymentMethod = paymentMethod,
        imageUrl = imageUrl
    )
}

internal fun Transaction.toDto() : TransactionDto {
    return TransactionDto(
        transactionID = transactionID,
        transactionType = transactionType,
        shippingAddress = shippingAddress,
        billingPayment = billingPayment,
        payment = payment.toDto()
    )
}

internal fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        transactionID = transactionID,
        transactionType = transactionType,
        shippingAddress = shippingAddress,
        billingPayment = billingPayment,
        payment = payment.toDomain()
    )
}

internal fun OrderDto.toDomain(): Order {
    return Order(
        orderID = orderID,
        orderDate = orderDate,
        orderStatus = orderStatus,
        userID = userID,
        totalAmount = totalAmount,
        transactionID = transactionID
    )
}