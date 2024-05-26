package com.kanyideveloper.joomia.di

import com.kanyideveloper.joomia.core.util.Constants.BASE_URL
import com.kanyideveloper.joomia.feature_auth.data.local.AuthPreferences
import com.kanyideveloper.joomia.feature_auth.data.remote.AuthApiService
import com.kanyideveloper.joomia.feature_auth.data.repository.AuthRepositoryImpl
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_auth.domain.use_case.AutoLoginUseCase
import com.kanyideveloper.joomia.feature_auth.domain.use_case.LoginUseCase
import com.kanyideveloper.joomia.feature_auth.domain.use_case.LogoutUseCase
import com.kanyideveloper.joomia.feature_auth.domain.use_case.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApiService(authInterceptor: AuthInterceptor): AuthApiService {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor(authInterceptor)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApiService: AuthApiService,
        authPreferences: AuthPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(
            authApiService = authApiService,
            authPreferences = authPreferences
        )
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository) : RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideAutoLoginUseCase(authRepository: AuthRepository): AutoLoginUseCase {
        return AutoLoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase {
        return LogoutUseCase(authRepository)
    }
}
