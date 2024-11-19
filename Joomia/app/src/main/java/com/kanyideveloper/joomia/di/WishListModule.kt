package com.kanyideveloper.joomia.di

import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Constants
import com.kanyideveloper.joomia.feature_auth.data.local.AuthPreferences
import com.kanyideveloper.joomia.feature_cart.data.remote.CartApiService
import com.kanyideveloper.joomia.feature_cart.data.repository.CartRepositoryImpl
import com.kanyideveloper.joomia.feature_cart.domain.repository.CartRepository
import com.kanyideveloper.joomia.feature_cart.domain.use_case.GetCartItemsUseCase
import com.kanyideveloper.joomia.feature_wishlist.data.remote.WishListApiService
import com.kanyideveloper.joomia.feature_wishlist.data.repository.WishListRepositoryImpl
import com.kanyideveloper.joomia.feature_wishlist.domain.repository.WishListRepository
import com.kanyideveloper.joomia.feature_wishlist.domain.use_case.GetWishListItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WishListModule {

    @Provides
    @Singleton
    fun provideWishListApiService(okHttpClient: OkHttpClient): WishListApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WishListApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWishListRepository(
        wishListApiService: WishListApiService
    ): WishListRepository {
        return WishListRepositoryImpl(
            wishListApiService
        )
    }

    @Provides
    @Singleton
    fun provideGetWishListItemsUseCase(
        wishListRepository: WishListRepository,
        authPreferences: AuthPreferences,
        gson: Gson
    ): GetWishListItemsUseCase {
        return GetWishListItemsUseCase(wishListRepository, authPreferences, gson)
    }
}