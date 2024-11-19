package com.kanyideveloper.joomia.feature_products.data.remote

import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import com.kanyideveloper.joomia.feature_products.data.remote.dto.OrderDto
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
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
    @GET("api/mobiles/{mobileID}")
    suspend fun getMobile(@Path("mobileID") id: Int) : MobileDto
    @POST("api/mobiles")
    suspend fun createMobile(@Body mobile: Mobile): Boolean
    @PATCH("api/mobiles/{mobileID}")
    suspend fun updateMobileDevice(@Path("mobileID") id: Int, @Body mobile: Mobile): Boolean
    @DELETE("api/mobiles/{mobileID}")
    suspend fun deleteMobileDevice(@Path("mobileID") id: Int): MutableMap<String, Boolean>

    // GET ALL orders by Admin role
    @GET("api/orders")
    suspend fun getAllOrders() : List<OrderDto>

    // GET ALL orders by User role
    @GET("api/orders/{userID}")
    suspend fun getAllOrdersByUserID(@Path("userID") userID: Int) : List<OrderDto>
    // GET Order by OrderID
    @GET("api/orders/order/{orderID}")
    suspend fun getOrderByOrderID(@Path("orderID") orderID: Int) : OrderDto
    // UPDATE Order
    @PATCH("api/orders/order/{orderID}")
    suspend fun updateOrder(@Path("orderID") orderID: Int, @Body orderStatus: String) : OrderDto
    // DELETE Order
    @DELETE("api/orders/order/{orderID}")
    suspend fun deleteOrder(@Path("orderID") orderID: Int) : MutableMap<String, Boolean>
}


