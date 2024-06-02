package com.kanyideveloper.joomia.feature_products.presentation.product_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.destinations.ProductDetailsScreenDestination
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import com.kanyideveloper.joomia.feature_profile.data.repository.ProfileRepository
import com.kanyideveloper.joomia.feature_profile.data.toDomain
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val gson: Gson
): ViewModel() {

    private val _profileState = mutableStateOf(User())

    private val _isAdminState = mutableStateOf(false)
    val isAdminState: State<Boolean> = _isAdminState

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val eventFlow: SharedFlow<UiEvents> = _eventFlow.asSharedFlow()

    init {
        getProfile()
        checkAdminAccount()
    }

    private fun checkAdminAccount() {
        viewModelScope.launch {
            _isAdminState.value = _profileState.value.userName?.let {
                authRepository.checkAdminAccount(it)
            } == true
        }
    }

    private fun getProfile() {
        viewModelScope.launch {
            profileRepository.getUserProfile().collectLatest { data ->
                val user = gson.fromJson(data, UserResponseDto::class.java)
                _profileState.value = user.toDomain()
            }
        }
    }

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