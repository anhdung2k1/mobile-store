package com.kanyideveloper.joomia.feature_products.domain.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getMobiles(): Flow<Resource<List<Mobile>>>
    suspend fun getMobileCategories(): List<String>
    suspend fun findMobileDeviceName(mobileName: String) : Flow<Resource<List<Mobile>>>
}