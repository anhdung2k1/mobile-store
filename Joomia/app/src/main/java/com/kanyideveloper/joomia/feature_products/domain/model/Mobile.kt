package com.kanyideveloper.joomia.feature_products.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mobile(
    val mobileType: String,
    val mobileDescription: String,
    val mobileID: Int,
    val imageUrl: String,
    val mobilePrice: Double,
    val rating: Rating,
    val mobileName: String
) : Parcelable
