package com.kanyideveloper.joomia.feature_products.data.remote

import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("api/mobiles")
    suspend fun createMobile(@Body mobile: Mobile): Boolean
}