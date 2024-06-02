package com.kanyideveloper.joomia.feature_cart.data.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_cart.data.remote.CartApiService
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_cart.domain.repository.CartRepository
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@Suppress("NAME_SHADOWING")
class CartRepositoryImpl(
    private val cartApiService: CartApiService
) : CartRepository {
    override suspend fun getAllCartItems(id: Int): Flow<Resource<List<CartMobile>>> = flow {
        Timber.d("Get all cart items called")
        emit(Resource.Loading())
        try {
            val response = cartApiService.cartItems(id)
            val cartItems = ArrayList<CartMobile>()
            response.cartMobileDtos.forEach {
                val productResponse = cartApiService.mobile(it.mobileID)
                val cartMobile =
                    CartMobile(
                        productResponse.mobileID,
                        productResponse.mobileName,
                        productResponse.mobilePrice,
                        it.mobileQuantity,
                        productResponse.imageUrl
                    )
                cartItems.add(cartMobile)

            }
            emit(Resource.Success(cartItems.toList().distinctBy { it.mobileName }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong! Seems nothing in your carts!"))
        }
    }

    override suspend fun createCartItems(id: Int, cartMobile: CartMobile): Flow<Resource<List<CartMobile>>> = flow {
        Timber.d("Create cart items called")
        emit(Resource.Loading())
        try {
            val response = cartApiService.createCartItem(id, cartMobile)
            val cartItems = ArrayList<CartMobile>()
            response.cartMobileDtos.forEach {
                val productResponse = cartApiService.mobile(it.mobileID)
                val cartMobile = CartMobile(
                    productResponse.mobileID,
                    productResponse.mobileName,
                    productResponse.mobilePrice,
                    it.mobileQuantity,
                    productResponse.imageUrl
                )
                cartItems.add(cartMobile)
            }
            emit(Resource.Success(cartItems.toList().distinctBy { it.mobileName }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong when update Carts!"))
        }
    }

    override suspend fun updateCartItems(id: Int, cartMobile: CartMobile): Flow<Resource<List<CartMobile>>> = flow {
        Timber.d("Update cart items called")
        emit(Resource.Loading())
        try {
            val response = cartApiService.updateCart(id, cartMobile)
            val cartItems = ArrayList<CartMobile>()
            response.cartMobileDtos.forEach {
                val productResponse = cartApiService.mobile(it.mobileID)
                val cartMobile = CartMobile(
                        productResponse.mobileID,
                        productResponse.mobileName,
                        productResponse.mobilePrice,
                        it.mobileQuantity,
                        productResponse.imageUrl
                    )
                cartItems.add(cartMobile)
            }
            emit(Resource.Success(cartItems.toList().distinctBy { it.mobileName }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong when update Carts!"))
        }
    }
}