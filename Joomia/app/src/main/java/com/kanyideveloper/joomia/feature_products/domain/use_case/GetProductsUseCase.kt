package com.kanyideveloper.joomia.feature_products.domain.use_case

import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(
    private val productsRepository: ProductsRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<Mobile>>> {
        return productsRepository.getMobiles()
    }
}