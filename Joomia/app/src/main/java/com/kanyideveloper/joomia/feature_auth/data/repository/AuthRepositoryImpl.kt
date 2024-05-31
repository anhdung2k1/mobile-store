package com.kanyideveloper.joomia.feature_auth.data.repository

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
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
import java.util.Date

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val authPreferences: AuthPreferences
) : AuthRepository {
    override suspend fun login(authRequest: AuthRequest): Resource<Unit> {
        Timber.d("Login called")
        return try {
            val response = authApiService.loginUser(authRequest)
            Timber.d("Login Token: ${response.token}")

            getUser(authRequest.userName)?.let { authPreferences.saveUserdata(it) }

            authPreferences.saveAccessToken(response.token)
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
            if (isTokenExpired(accessToken)) {
                Timber.d("The token has been expired, called clear access token")
                authPreferences.clearAccessToken()
                Resource.Error("The Token has been cleared, navigate to login site")
            } else {
                Resource.Success(Unit)
            }
        } else {
            Resource.Error("Access token is empty")
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

    override suspend fun checkAdminAccount(userName: String): Boolean {
        val isAdmin = authApiService.checkAdminAccount(userName)
        Timber.d("checkAdminAccount($userName): $isAdmin")
        return isAdmin
    }

    private suspend fun getUser(name: String): UserResponseDto? {
        val response = authApiService.getAllUsers()
        return response.find { it.userName == name }
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            val expriresAt: Date? = decodedJWT.expiresAt
            if (expriresAt != null) {
                val now = Date()
                return expriresAt.before(now)
            }
            return false
        } catch (e: Exception) {
            // Handle the exception as needed (e.g., log it, return true to indicate the token is invalid)
            Timber.e(e, "Token expiration check failed")
            true
        }
    }
}