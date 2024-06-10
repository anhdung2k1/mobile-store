package com.kanyideveloper.joomia.feature_profile.data

import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_profile.domain.model.User

internal fun UserResponseDto.toDomain(): User {
    return User(
        id = id,
        userName = userName,
        address = address,
        birthDay = birthDay,
        gender = gender,
        imageUrl = imageUrl
    )
}

internal fun User.toDto(): UserResponseDto {
    return UserResponseDto(
        id = id ?: 0,  // Provide a default value for id if it is null
        userName = userName.orEmpty(),  // Use orEmpty() to provide a default value for userName if it is null
        address = address.orEmpty(),  // Use orEmpty() to provide a default value for address if it is null
        birthDay = birthDay.orEmpty(),  // Use orEmpty() to provide a default value for birthDay if it is null
        gender = gender.orEmpty(),  // Use orEmpty() to provide a default value for gender if it is null
        imageUrl = imageUrl.orEmpty()  // Use orEmpty() to provide a default value for imageUrl if it is null
    )
}