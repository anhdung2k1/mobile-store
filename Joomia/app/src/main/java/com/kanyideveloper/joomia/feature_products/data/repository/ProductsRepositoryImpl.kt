package com.kanyideveloper.joomia.feature_products.data.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_products.data.remote.ProductsApiService
import com.kanyideveloper.joomia.feature_products.data.remote.mappers.toDomain
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Order
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ProductsRepositoryImpl(private val productsApiService: ProductsApiService) :
    ProductsRepository {
    override suspend fun getMobiles(): Flow<Resource<List<Mobile>>> = flow {
        emit(Resource.Loading())
        try {
            val response = productsApiService.getMobiles()
            emit(Resource.Success(response.map { it.toDomain() }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        }
    }

    override suspend fun getMobileCategories(): List<String> {
        return productsApiService.getMobilesCategories()
    }

    override suspend fun findMobileDeviceName(mobileName: String) : Flow<Resource<List<Mobile>>> = flow {
        emit(Resource.Loading())
        try {
            val response = productsApiService.findMobileDeviceName(mobileName)
            emit(Resource.Success(response.map { it.toDomain() }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        }
    }

    override suspend fun getMobileDevice(mobileID: Int): Mobile {
        return productsApiService.getMobile(mobileID).toDomain()
    }

    override suspend fun createMobileDevice(mobile: Mobile): Boolean {
        return productsApiService.createMobile(mobile)
    }

    override suspend fun updateMobileDevice(mobileID: Int, mobile: Mobile): Boolean {
        return productsApiService.updateMobileDevice(mobileID, mobile)
    }

    override suspend fun deleteMobileDevice(mobileID: Int): MutableMap<String, Boolean> {
        return productsApiService.deleteMobileDevice(mobileID)
    }

    // Get Orders by ADMIN
    override suspend fun getOrders(): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        try {
            val response = productsApiService.getAllOrders()
            emit(Resource.Success(response.map { it.toDomain() }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        }
    }

    // Get Orders by USER
    override suspend fun getOrdersByUserID(userID: Int): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        try {
            val response = productsApiService.getAllOrdersByUserID(userID)
            emit(Resource.Success(response.map { it.toDomain() }))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Could not reach the server, please check your internet connection!"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Oops, something went wrong!"))
        }
    }

    override suspend fun getOrderByOrderID(orderID: Int): Order {
        return productsApiService.getOrderByOrderID(orderID).toDomain()
    }

    override suspend fun updateOrder(orderID: Int, orderStatus: String): Order {
        return productsApiService.updateOrder(orderID, orderStatus).toDomain()
    }

    override suspend fun deleteOrder(orderID: Int): MutableMap<String, Boolean> {
        return productsApiService.deleteOrder(orderID)
    }
}