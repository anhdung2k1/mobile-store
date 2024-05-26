package com.kanyideveloper.joomia.feature_auth.data.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.data.local.AuthPreferences
import com.kanyideveloper.joomia.feature_auth.data.remote.AuthApiService
import com.kanyideveloper.joomia.feature_auth.data.remote.request.AuthRequest
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val authPreferences: AuthPreferences
) : AuthRepository {
    override suspend fun login(authRequest: AuthRequest, rememberMe: Boolean): Resource<Unit> {
        Timber.d("Login called")
        return try {
            val response = authApiService.loginUser(authRequest)
            Timber.d("Login Token: ${response.token}")

            getUser(authRequest.userName)?.let { authPreferences.saveUserdata(it) }

            if (rememberMe) {
                authPreferences.saveAccessToken(response.token)
            }
            Resource.Success(Unit)
        } catch (e: IOException) {
            Resource.Error(message = "Could not reach the server, please check your internet connection!")
        } catch (e: HttpException) {
            Resource.Error(message = "An Unknown error occurred, please try again!")
        }
    }

    override suspend fun register(authRequest: AuthRequest) : Resource<Unit> {
        Timber.d("Register called")
        return try {
            val response = authApiService.registerUser(authRequest)
            Timber.d("Register Token: ${response.token}")

            getUser(authRequest.userName)?.let { authPreferences.saveUserdata(it) }
            authPreferences.saveAccessToken(response.token)

            Resource.Success(Unit)
        } catch (e: IOException) {
            Resource.Error(message = "Could not reach the server, please check your internet connection!")
        } catch (e : HttpException) {
            Resource.Error(message = "An Unknown error occurred, please try again!")
        }
    }

    override suspend fun autoLogin(): Resource<Unit> {
        val accessToken = authPreferences.getAccessToken.first()
        Timber.d("Auto login access token: $accessToken")
        return if (accessToken.isNotEmpty()) {
            Resource.Success(Unit)
        } else {
            Resource.Error("")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            authPreferences.clearAccessToken()
            val fetchedToken = authPreferences.getAccessToken.first()
            Timber.d("token: $fetchedToken")

            if (fetchedToken.isEmpty()) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Resource.Error("Unknown error occurred")
        }
    }

    private suspend fun getUser(name: String): UserResponseDto? {
        val response = authApiService.getAllUsers()
        return response.find { it.userName == name }
    }
}