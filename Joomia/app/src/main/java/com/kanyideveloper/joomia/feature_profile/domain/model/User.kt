package com.kanyideveloper.joomia.feature_profile.domain.model

import java.util.Date

data class User(
    val id: Int? = null,
    val userName: String? = null,
    var birthDay: Date? = null,
    var address: String? = null,
    var gender: String? = null,
    val imageUrl: String? = null
)
