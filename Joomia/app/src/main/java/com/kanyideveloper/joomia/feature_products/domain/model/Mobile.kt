package com.kanyideveloper.joomia.feature_products.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mobile(
    val mobileID: Int,
    val mobileType: String,
    val mobileDescription: String,
    val imageUrl: String,
    val mobilePrice: Double,
    val mobileQuantity: Int,
    val rating: Rating,
    val mobileName: String,
    val mobileModel: String
) : Parcelable
