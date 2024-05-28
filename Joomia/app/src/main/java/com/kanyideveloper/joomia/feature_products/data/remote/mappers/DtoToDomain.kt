package com.kanyideveloper.joomia.feature_products.data.remote.mappers

import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import com.kanyideveloper.joomia.feature_products.data.remote.dto.RatingDto
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Rating

internal fun MobileDto.toDomain(): Mobile {
    return Mobile(
        mobileType = mobileType,
        mobileDescription = mobileDescription,
        mobileID = mobileID,
        imageUrl = imageUrl,
        mobilePrice = mobilePrice,
        rating = ratingDto.toDomain(),
        mobileName = mobileName
    )
}

internal fun RatingDto.toDomain(): Rating {
    return Rating(
        count = count,
        rate = rate
    )
}