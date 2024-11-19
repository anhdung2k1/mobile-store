package com.kanyideveloper.joomia.feature_profile.presentation.account

import com.kanyideveloper.joomia.feature_profile.domain.model.User

data class ProfileState(
    var profiles: User? = User(),
    val isLoading: Boolean = false,
    val error: String? = null
)