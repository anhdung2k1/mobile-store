package com.kanyideveloper.joomia.feature_products.presentation.product_adding

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Rating
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import com.kanyideveloper.joomia.feature_products.domain.use_case.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ProductAddingViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _categoriesState = mutableStateOf(emptyList<String>())
    val categoriesState: State<List<String>> = _categoriesState

    private val _isProductCreatedState = mutableStateOf(false)

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

    private fun compressImage(context: Context, uri: Uri): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inSampleSize = 2 // Adjust this value as needed for compression
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            Timber.e("Error compressing image: $e")
        } finally {
            inputStream?.close()
        }
        return null
    }

    private fun convertImageToBase64(context: Context, uri: Uri): String {
        val bitmap = compressImage(context, uri)
        return if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Adjust compression quality as needed
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } else {
            ""
        }
    }


    fun createProduct(
        context: Context,
        productID: String,
        productName: String,
        productModel: String,
        productType: String,
        productDescription: String,
        productPrice: String,
        productQuantity: String,
        selectedImageUri: Uri?
    ) {
        // Create a Mobile object with the provided details
        val mobile = Mobile(
            mobileID = productID.toIntOrNull() ?: 0,
            mobileName = productName,
            mobileType = productType,
            mobileDescription = productDescription,
            mobilePrice = productPrice.toDoubleOrNull() ?: 0.0,
            mobileQuantity = productQuantity.toIntOrNull() ?: 0,
            mobileModel = productModel, // Fill in the appropriate value for the mobile model
            imageUrl = selectedImageUri?.let { convertImageToBase64(context, it) } ?: "", // Convert Uri to string
            rating = Rating(0,0.0), // Create an appropriate Rating object
        )

        // Call the repository method to create the product
        viewModelScope.launch {
            try {
                Timber.d("createProduct(): Called")
                Timber.d("Mobile model: $mobile")
                _isProductCreatedState.value = productsRepository.createMobileDevice(mobile)
                Timber.d("Check product created: ${_isProductCreatedState.value}")
                if (_isProductCreatedState.value) {
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