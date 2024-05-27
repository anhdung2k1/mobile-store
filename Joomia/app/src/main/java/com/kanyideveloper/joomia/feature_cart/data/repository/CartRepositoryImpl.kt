package com.kanyideveloper.joomia.feature_cart.data.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_cart.data.remote.CartApiService
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class CartRepositoryImpl(
    private val cartApiService: CartApiService
) : CartRepository {
    override suspend fun getAllCartItems(id: Int): Flow<Resource<List<CartMobile>>> = flow {
        Timber.d("Get all cart items called")
        emit(Resource.Loading())
        try {
            val response = cartApiService.cartItems(id)
            val cartItems = ArrayList<CartMobile>()
            response.forEach { it1 ->
                it1.cartMobileDtos.forEach {
                    val productResponse = cartApiService.mobile(it.mobileID)
                    val cartMobile =
                        CartMobile(
                            productResponse.title,
                            productResponse.price,
                            it.mobileQuantity,
                            productResponse.image
                        )
                    cartItems.add(cartMobile)
                }
            }
            emit(Resource.Success(cartItems.toList().distinctBy { it.mobileName }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        }
    }
}