package com.kanyideveloper.joomia.feature_auth.domain.use_case

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return authRepository.logout()
    }
}
