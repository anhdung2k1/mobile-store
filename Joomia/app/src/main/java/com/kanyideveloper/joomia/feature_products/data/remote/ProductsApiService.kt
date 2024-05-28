package com.kanyideveloper.joomia.feature_products.data.remote

import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import retrofit2.http.GET

interface ProductsApiService {

    @GET("api/mobiles")
    suspend fun getMobiles(): List<MobileDto>

    @GET("api/mobiles/categories")
    suspend fun getMobilesCategories(): List<String>
}