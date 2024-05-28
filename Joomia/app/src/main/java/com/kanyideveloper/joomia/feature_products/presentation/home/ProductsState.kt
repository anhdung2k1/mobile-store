package com.kanyideveloper.joomia.feature_products.presentation.home

import com.kanyideveloper.joomia.feature_products.domain.model.Mobile

data class ProductsState(
    val mobiles: List<Mobile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)