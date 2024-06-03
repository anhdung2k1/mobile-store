package com.kanyideveloper.joomia.feature_wishlist.data.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_wishlist.data.remote.WishListApiService
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile
import com.kanyideveloper.joomia.feature_wishlist.domain.repository.WishListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@Suppress("NAME_SHADOWING")
class WishListRepositoryImpl(
    private val wishListApiService: WishListApiService
) : WishListRepository {
    override suspend fun getAllWishListItems(id: Int): Flow<Resource<List<WishListMobile>>> = flow {
        Timber.d("Get all cart items called")
        emit(Resource.Loading())
        try {
            val response = wishListApiService.wishListItems(id)
            val wishListItems = ArrayList<WishListMobile>()
            response.wishListMobileDtos.forEach {
                val productResponse = wishListApiService.mobile(it.mobileID)
                val wishListMobile =
                    WishListMobile(
                        productResponse.mobileID,
                        productResponse.mobileName,
                        productResponse.mobilePrice,
                        it.mobileQuantity,
                        productResponse.imageUrl
                    )
                wishListItems.add(wishListMobile)

            }
            emit(Resource.Success(wishListItems.toList().distinctBy { it.mobileName }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong! Seems nothing in your carts!"))
        }
    }

    override suspend fun createWishListItems(id: Int, wishListMobile: WishListMobile): Flow<Resource<List<WishListMobile>>> = flow {
        Timber.d("Create cart items called")
        emit(Resource.Loading())
        try {
            val response = wishListApiService.createWishListItem(id, wishListMobile)
            val wishListItems = ArrayList<WishListMobile>()
            response.wishListMobileDtos.forEach {
                val productResponse = wishListApiService.mobile(it.mobileID)
                val wishListMobile = WishListMobile(
                    productResponse.mobileID,
                    productResponse.mobileName,
                    productResponse.mobilePrice,
                    it.mobileQuantity,
                    productResponse.imageUrl
                )
                wishListItems.add(wishListMobile)
            }
            emit(Resource.Success(wishListItems.toList().distinctBy { it.mobileName }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong when update Carts!"))
        }
    }

    override suspend fun updateWishListItems(id: Int, wishListMobile: WishListMobile): Flow<Resource<List<WishListMobile>>> = flow {
        Timber.d("Update cart items called")
        emit(Resource.Loading())
        try {
            val response = wishListApiService.updateWishList(id, wishListMobile)
            val wishListItems = ArrayList<WishListMobile>()
            response.wishListMobileDtos.forEach {
                val productResponse = wishListApiService.mobile(it.mobileID)
                val wishListMobile = WishListMobile(
                    productResponse.mobileID,
                    productResponse.mobileName,
                    productResponse.mobilePrice,
                    it.mobileQuantity,
                    productResponse.imageUrl
                )
                wishListItems.add(wishListMobile)
            }
            emit(Resource.Success(wishListItems.toList().distinctBy { it.mobileName }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong when update Carts!"))
        }
    }
}