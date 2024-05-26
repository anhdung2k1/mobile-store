package com.kanyideveloper.joomia.feature_profile.domain.model

import java.util.Date

data class User(
    val id: Int? = null,
    val userName: String? = null,
    val birthDay: Date? = null,
    val address: String? = null,
    val gender: String? = null
)
