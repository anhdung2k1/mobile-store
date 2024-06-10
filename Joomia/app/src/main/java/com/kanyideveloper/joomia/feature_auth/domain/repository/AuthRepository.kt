package com.kanyideveloper.joomia.feature_auth.domain.repository

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_auth.data.remote.request.AuthRequest
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(authRequest: AuthRequest): Resource<Unit>
    suspend fun register(authRequest: AuthRequest) : Resource<Unit>
    suspend fun autoLogin(): Resource<Unit>
    suspend fun logout(): Resource<Unit>
    suspend fun checkAdminAccount(userName: String): Boolean
    suspend fun getAccountIdByUserName(userName: String): Int
    suspend fun updateAccountPassword(accountId: Int, newPassword: String): Boolean
    suspend fun updateUser(userId: Int, user: User): Flow<Resource<User>>
    suspend fun getUserProfile(): Flow<String>
    suspend fun getUserProfileByUserId(userId: Int): Flow<Resource<User>>
}
