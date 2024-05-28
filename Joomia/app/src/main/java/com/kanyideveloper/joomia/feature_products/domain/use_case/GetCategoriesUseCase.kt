package com.kanyideveloper.joomia.feature_products.domain.use_case

import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository

class GetCategoriesUseCase(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(): List<String> {
        val categories = productsRepository.getMobileCategories()
        return listOf("All") + categories
    }
}