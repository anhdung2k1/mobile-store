package com.kanyideveloper.joomia.feature_products.data.remote

import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductsApiService {
    // Get All Mobile devices
    @GET("api/mobiles")
    suspend fun getMobiles(): List<MobileDto>
    // Get all mobile categories
    @GET("api/mobiles/categories")
    suspend fun getMobilesCategories(): List<String>
    // Search mobile with mobile Name
    @GET("api/mobiles/products/query")
    suspend fun findMobileDeviceName(@Query("query") mobileName: String) : List<MobileDto>
}