package com.kanyideveloper.joomia.feature_wishlist.data.remote

import com.kanyideveloper.joomia.feature_products.data.remote.dto.MobileDto
import com.kanyideveloper.joomia.feature_wishlist.data.remote.dto.UserWishListResponseDto
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface WishListApiService {
    @GET("api/wishlist/user/{id}")
    suspend fun wishListItems(
        @Path("id") userId: Int
    ): UserWishListResponseDto

    @GET("api/mobiles/{id}")
    suspend fun mobile(
        @Path("id") id: Int
    ): MobileDto

    @POST("api/wishlist/user/{id}")
    suspend fun createWishListItem(@Path("id") id: Int, @Body wishListMobile: WishListMobile): UserWishListResponseDto

    @PATCH("api/wishlist/user/{id}")
    suspend fun updateWishList(@Path("id") id: Int, @Body wishListMobile: WishListMobile): UserWishListResponseDto
}