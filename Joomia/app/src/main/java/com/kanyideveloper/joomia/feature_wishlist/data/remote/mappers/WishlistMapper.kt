package com.kanyideveloper.joomia.feature_wishlist.data.remote.mappers

import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Rating
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile

internal fun WishListMobile.toProduct(): Mobile {
    return Mobile(
        mobileID = mobileID,
        mobileName = mobileName,
        mobilePrice = mobilePrice,
        mobileQuantity = mobileQuantity,
        imageUrl = imageUrl,
        mobileModel = "",
        mobileDescription = "",
        mobileType = "",
        rating = Rating(0, 0.0)
    )
}