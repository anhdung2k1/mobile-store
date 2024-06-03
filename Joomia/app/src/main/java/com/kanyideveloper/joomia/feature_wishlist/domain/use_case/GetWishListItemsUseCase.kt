package com.kanyideveloper.joomia.feature_wishlist.domain.use_case

import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.data.local.AuthPreferences
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile
import com.kanyideveloper.joomia.feature_wishlist.domain.repository.WishListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetWishListItemsUseCase(
    private val wishListRepository: WishListRepository,
    private val authPreferences: AuthPreferences,
    private val gson: Gson

) {
    suspend operator fun invoke(): Flow<Resource<List<WishListMobile>>> {
        val data = authPreferences.getUserData.first()
        val user = gson.fromJson(data, UserResponseDto::class.java)
        return wishListRepository.getAllWishListItems(user.id)
    }
}