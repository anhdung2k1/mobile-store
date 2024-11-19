package com.kanyideveloper.joomia.feature_cart.data.remote.dto.PayPal

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("intent")
    val intent: String,

    @SerializedName("purchase_units")
    val purchaseUnits: List<PurchaseUnit>
)

data class PurchaseUnit(
    @SerializedName("amount")
    val amount: Amount
)

data class Amount(
    @SerializedName("currency_code")
    val currencyCode: String,

    @SerializedName("value")
    val value: String
)