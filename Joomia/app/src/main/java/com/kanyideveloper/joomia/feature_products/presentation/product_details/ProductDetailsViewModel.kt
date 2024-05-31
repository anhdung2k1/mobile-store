package com.kanyideveloper.joomia.feature_products.presentation.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val eventFlow: SharedFlow<UiEvents> = _eventFlow.asSharedFlow()
    fun deleteProduct(
        mobileID: Int
    ) {
        viewModelScope.launch {
            try {
                Timber.d("deleteProduct(): called")
                Timber.d("mobileID: $mobileID")
                val isDeleted = productsRepository.deleteMobileDevice(mobileID)["deleted"]

                Timber.d("isDeleted: $isDeleted")
                if (isDeleted == true) {
                    _eventFlow.emit(
                        UiEvents.NavigateEvent(
                            HomeScreenDestination.route
                        )
                    )
                } else {
                    _eventFlow.emit(
                        UiEvents.SnackbarEvent(
                            message = "Error to create the product"
                        )
                    )
                }
            } catch (e: Exception) {
                _eventFlow.emit(
                    UiEvents.SnackbarEvent(
                        message = "Unknown error occurred"
                    )
                )
            }
        }
    }
}