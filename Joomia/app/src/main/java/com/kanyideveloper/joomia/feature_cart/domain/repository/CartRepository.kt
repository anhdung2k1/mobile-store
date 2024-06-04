package com.kanyideveloper.joomia.feature_cart.domain.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_cart.domain.model.Payment
import com.kanyideveloper.joomia.feature_cart.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun getAllCartItems(id: Int): Flow<Resource<List<CartMobile>>>
    suspend fun createCartItems(id: Int, cartMobile: CartMobile): Flow<Resource<List<CartMobile>>>
    suspend fun updateCartItems(id: Int, cartMobile: CartMobile): Flow<Resource<List<CartMobile>>>
    suspend fun getAllPayments(): Flow<Resource<List<Payment>>>
    suspend fun createTransaction(userId: Int, transaction: Transaction): Boolean
}