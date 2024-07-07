package com.kanyideveloper.joomia.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Constants
import com.kanyideveloper.joomia.feature_auth.data.local.AuthPreferences
import com.kanyideveloper.joomia.feature_auth.util.Constants.AUTH_PREFERENCES
import com.kanyideveloper.joomia.feature_cart.data.remote.PayPalApiService
import com.kanyideveloper.joomia.feature_cart.data.repository.PayPalRepositoryImpl
import com.kanyideveloper.joomia.feature_cart.domain.repository.PayPalRepository
import com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal.CaptureOrderUseCase
import com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal.CreateOrderUseCase
import com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal.GetPayPalAccessTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferenceDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile(AUTH_PREFERENCES)
            }
        )

    @Provides
    @Singleton
    fun provideAuthPreferences(dataStore: DataStore<Preferences>, gson: Gson) =
        AuthPreferences(dataStore, gson)

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideAuthInterceptor(authPreferences: AuthPreferences) : AuthInterceptor {
        return AuthInterceptor(authPreferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    @Provides
    @Singleton
    fun providePayPalApiService(): PayPalApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.sandbox.paypal.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PayPalApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePayPalRepository(apiService: PayPalApiService): PayPalRepository {
        val clientId = Constants.PAYPAL_CLIENT_ID
        val secret = Constants.PAYPAL_CLIENT_SECRET
        return PayPalRepositoryImpl(
            apiService,
            clientId,
            secret
        )
    }

    @Provides
    @Singleton
    fun provideGetAccessTokenUseCase(repository: PayPalRepository): GetPayPalAccessTokenUseCase {
        return GetPayPalAccessTokenUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateOrderUseCase(repository: PayPalRepository): CreateOrderUseCase {
        return CreateOrderUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCaptureOrderUseCase(repository: PayPalRepository): CaptureOrderUseCase {
        return CaptureOrderUseCase(repository)
    }
}