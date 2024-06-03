package com.kanyideveloper.joomia.feature_wishlist.presentation

import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile

data class WishListItemsState (
    val wishListItems: List<WishListMobile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)