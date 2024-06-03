package com.kanyideveloper.joomia.feature_wishlist.domain.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile
import kotlinx.coroutines.flow.Flow

interface WishListRepository {
    suspend fun getAllWishListItems(id: Int): Flow<Resource<List<WishListMobile>>>
    suspend fun createWishListItems(id: Int, wishListMobile: WishListMobile): Flow<Resource<List<WishListMobile>>>
    suspend fun updateWishListItems(id: Int, wishListMobile: WishListMobile): Flow<Resource<List<WishListMobile>>>
}