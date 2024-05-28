package com.kanyideveloper.joomia.feature_products.data.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_products.data.remote.ProductsApiService
import com.kanyideveloper.joomia.feature_products.data.remote.mappers.toDomain
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
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
}