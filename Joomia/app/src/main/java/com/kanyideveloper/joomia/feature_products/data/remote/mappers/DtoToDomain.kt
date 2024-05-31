package com.kanyideveloper.joomia.feature_products.data.remote.mappers

import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import com.kanyideveloper.joomia.feature_products.data.remote.dto.RatingDto
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
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