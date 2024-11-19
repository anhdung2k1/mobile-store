package com.kanyideveloper.joomia.feature_products.domain.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getMobiles(): Flow<Resource<List<Mobile>>>
    suspend fun getMobileCategories(): List<String>
    suspend fun findMobileDeviceName(mobileName: String) : Flow<Resource<List<Mobile>>>
    suspend fun getMobileDevice(mobileID: Int): Mobile
    suspend fun createMobileDevice(mobile: Mobile) : Boolean
    suspend fun updateMobileDevice(mobileID: Int, mobile: Mobile): Boolean
    suspend fun deleteMobileDevice(mobileID: Int): MutableMap<String, Boolean>
    // Get Orders with ADMIN
    suspend fun getOrders(): Flow<Resource<List<Order>>>
    // Get Orders with USER
    suspend fun getOrdersByUserID(userID: Int): Flow<Resource<List<Order>>>
    // Get Order By OrderID
    suspend fun getOrderByOrderID(orderID: Int): Order
    // UPDATE Order
    suspend fun updateOrder(orderID: Int, orderStatus: String): Order
    // DELETE Order
    suspend fun deleteOrder(orderID: Int): MutableMap<String, Boolean>
}