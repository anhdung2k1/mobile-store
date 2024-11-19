package com.kanyideveloper.joomia.di

import com.kanyideveloper.joomia.feature_auth.data.local.AuthPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val authPreferences: AuthPreferences) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val token = runBlocking { authPreferences.getAccessToken.firstOrNull() }

        return if (token != null) {
            val newRequest = originRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originRequest)
        }
    }
}