package com.kanyideveloper.joomia.feature_wishlist.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_products.domain.model.Rating
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import com.kanyideveloper.joomia.feature_profile.data.toDomain
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile
import com.kanyideveloper.joomia.feature_wishlist.domain.repository.WishListRepository
import com.kanyideveloper.joomia.feature_wishlist.domain.use_case.GetWishListItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val getWishListItemsUseCase: GetWishListItemsUseCase,
    private val wishListRepository: WishListRepository,
    private val authRepository: AuthRepository,
    private val productsRepository: ProductsRepository,
    private val gson: Gson
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val evenFlow: SharedFlow<UiEvents> = _eventFlow.asSharedFlow()

    private val _profileState = mutableStateOf(User())
    val profilState: State<User> = _profileState

    private val _mobileState = mutableStateOf(Mobile(0, "", "", "", 0.0, 0, Rating(0,0.0), "", ""))
    val mobileState: State<Mobile> = _mobileState

    private val _state = mutableStateOf(WishListItemsState())
    val state: State<WishListItemsState> = _state

    init {
        viewModelScope.launch {
            getWishListItems()
            getProfile()
        }
    }

    private suspend fun getProfile() {
        viewModelScope.launch {
            authRepository.getUserProfile().collectLatest { data ->
                val user = gson.fromJson(data, UserResponseDto::class.java)
                _profileState.value = user.toDomain()
            }
        }
    }

    suspend fun getMobileDevice(mobileID: Int): Mobile {
        return productsRepository.getMobileDevice(mobileID)
    }

    suspend fun updateWishListItem(wishListMobile: WishListMobile) {
        viewModelScope.launch {
            _profileState.value.id?.let {
                wishListRepository.updateWishListItems(it, wishListMobile).collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                wishListItems = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = true
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
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

    suspend fun removeAllWishListItems(userID: Int) {
        viewModelScope.launch {
            wishListRepository.removeAllWishListItems(userID)
        }
    }

    private suspend fun getWishListItems() {
        getWishListItemsUseCase().collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        wishListItems = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        isLoading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
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