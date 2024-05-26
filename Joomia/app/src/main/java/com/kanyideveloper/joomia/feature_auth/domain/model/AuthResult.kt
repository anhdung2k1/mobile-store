package com.kanyideveloper.joomia.feature_auth.domain.model

import com.kanyideveloper.joomia.core.util.Resource

data class AuthResult(
    val passwordError: String? = null,
    val userNameError: String? = null,
    val result: Resource<Unit>? = null
)
