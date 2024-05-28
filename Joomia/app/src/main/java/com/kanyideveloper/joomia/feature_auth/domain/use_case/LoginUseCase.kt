package com.kanyideveloper.joomia.feature_auth.domain.use_case

import com.kanyideveloper.joomia.feature_auth.data.remote.request.AuthRequest
import com.kanyideveloper.joomia.feature_auth.domain.model.AuthResult
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        userName: String,
        password: String,
        rememberMe: Boolean
    ): AuthResult {
        val userNameError = if (userName.isBlank()) "User name cannot be blank" else null
        val passwordError = if (password.isBlank()) "Password cannot be blank" else null


        if (userNameError != null) {
            return AuthResult(
                userNameError = userNameError
            )
        }

        if (passwordError != null) {
            return AuthResult(
                passwordError = passwordError
            )
        }


        val authRequest = AuthRequest(
            userName = userName.trim(),
            password = password.trim()
        )

        return AuthResult(
            result = authRepository.login(authRequest)
        )
    }
}