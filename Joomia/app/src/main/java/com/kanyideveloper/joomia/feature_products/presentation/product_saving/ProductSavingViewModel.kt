package com.kanyideveloper.joomia.feature_products.presentation.product_saving

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanyideveloper.joomia.Utils.Utils
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Rating
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import com.kanyideveloper.joomia.feature_products.domain.use_case.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductSavingViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _categoriesState = mutableStateOf(emptyList<String>())
    val categoriesState: State<List<String>> = _categoriesState

    private val _isProductSavedState = mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val eventFlow: SharedFlow<UiEvents> = _eventFlow.asSharedFlow()

    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            _categoriesState.value = getCategoriesUseCase()
        }
    }

    fun saveProduct(
        context: Context,
        productID: Int,
        productName: String,
        productModel: String,
        productType: String,
        productDescription: String,
        productPrice: String,
        productQuantity: String,
        selectedImageUri: Uri?,
        isUpdate: Boolean
    ) {
        // Create a Mobile object with the provided details
        val mobile = Mobile(
            mobileID = productID,
            mobileName = productName,
            mobileType = productType,
            mobileDescription = productDescription,
            mobilePrice = productPrice.toDoubleOrNull() ?: 0.0,
            mobileQuantity = productQuantity.toIntOrNull() ?: 0,
            mobileModel = productModel, // Fill in the appropriate value for the mobile model
            imageUrl = selectedImageUri?.let { Utils.convertImageToBase64(context, it) } ?: "", // Convert Uri to string
            rating = Rating(0,0.0), // Create an appropriate Rating object
        )

        // Call the repository method to create the product
        viewModelScope.launch {
            try {
                Timber.d("savingProduct(): Called")
                Timber.d("Mobile model: $mobile")
                if (isUpdate) {
                    Timber.d("Update product(): Called")
                    _isProductSavedState.value = productsRepository.updateMobileDevice(mobile.mobileID, mobile)
                } else {
                    Timber.d("Create product(): Called")
                    _isProductSavedState.value = productsRepository.createMobileDevice(mobile)
                }
                Timber.d("Check product saved: ${_isProductSavedState.value}")
                if (_isProductSavedState.value) {
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