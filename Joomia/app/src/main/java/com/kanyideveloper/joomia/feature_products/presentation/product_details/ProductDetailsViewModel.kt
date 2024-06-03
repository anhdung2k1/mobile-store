package com.kanyideveloper.joomia.feature_products.presentation.product_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.destinations.ProductDetailsScreenDestination
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_cart.domain.repository.CartRepository
import com.kanyideveloper.joomia.feature_cart.presentation.cart.CartItemsState
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import com.kanyideveloper.joomia.feature_profile.data.repository.ProfileRepository
import com.kanyideveloper.joomia.feature_profile.data.toDomain
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile
import com.kanyideveloper.joomia.feature_wishlist.domain.repository.WishListRepository
import com.kanyideveloper.joomia.feature_wishlist.presentation.WishListItemsState
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
    private val cartRepository: CartRepository,
    private val wishListRepository: WishListRepository,
    private val gson: Gson
): ViewModel() {

    private val _profileState = mutableStateOf(User())

    private val _cartState = mutableStateOf(CartItemsState())
    val cartState: State<CartItemsState> = _cartState

    private val _wishListState = mutableStateOf(WishListItemsState())
    val wishListState: State<WishListItemsState> = _wishListState

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

    suspend fun createWishListItem(wishListMobile: WishListMobile) {
        viewModelScope.launch {
            _profileState.value.id?.let {
                wishListRepository.createWishListItems(it, wishListMobile).collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _wishListState.value = wishListState.value.copy(
                                wishListItems = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _wishListState.value = wishListState.value.copy(
                                isLoading = true
                            )
                        }

                        is Resource.Error -> {
                            _wishListState.value = wishListState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                            _eventFlow.emit(
                                UiEvents.SnackbarEvent(
                                    message = result.message ?: "Unknown error occurred!"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    suspend fun createCartItem(cartMobile: CartMobile) {
        viewModelScope.launch {
            _profileState.value.id?.let {
                cartRepository.createCartItems(it, cartMobile).collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _cartState.value = cartState.value.copy(
                                cartItems = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _cartState.value = cartState.value.copy(
                                isLoading = true
                            )
                        }

                        is Resource.Error -> {
                            _cartState.value = cartState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                            _eventFlow.emit(
                                UiEvents.SnackbarEvent(
                                    message = result.message ?: "Unknown error occurred!"
                                )
                            )
                        }
                    }
                }
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