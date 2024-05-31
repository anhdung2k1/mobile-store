package com.kanyideveloper.joomia.feature_auth.domain.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_auth.data.remote.request.AuthRequest

interface AuthRepository {
    suspend fun login(authRequest: AuthRequest): Resource<Unit>
    suspend fun register(authRequest: AuthRequest) : Resource<Unit>
    suspend fun autoLogin(): Resource<Unit>
    suspend fun logout(): Resource<Unit>
    suspend fun checkAdminAccount(userName: String): Boolean
}
