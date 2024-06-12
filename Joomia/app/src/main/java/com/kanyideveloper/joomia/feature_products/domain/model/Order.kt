package com.kanyideveloper.joomia.feature_products.domain.model

import com.kanyideveloper.joomia.feature_cart.domain.model.Transaction
import com.kanyideveloper.joomia.feature_profile.domain.model.User

data class Order (
    val orderID: Int,
    val userID: Int,
    val transactionID: Int,
    val orderDate: String,
    var orderStatus: String,
    val totalAmount: Double
)