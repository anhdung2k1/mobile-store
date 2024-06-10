package com.kanyideveloper.joomia.feature_products.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_cart.domain.repository.CartRepository
import com.kanyideveloper.joomia.feature_cart.presentation.cart.CartItemsState
import com.kanyideveloper.joomia.feature_products.domain.use_case.FindProductsUseCase
import com.kanyideveloper.joomia.feature_products.domain.use_case.GetCategoriesUseCase
import com.kanyideveloper.joomia.feature_products.domain.use_case.GetProductsUseCase
import com.kanyideveloper.joomia.feature_profile.data.toDomain
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val findProductsUseCase: FindProductsUseCase,
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    private val gson: Gson
) :
    ViewModel() {

    private val _isAdminState = mutableStateOf(false)
    val isAdminState: State<Boolean> = _isAdminState

    private val _cartState = mutableStateOf(CartItemsState())
    val cartState: State<CartItemsState> = _cartState

    private val _profileState = mutableStateOf(User())
    val profileState: State<User> = _profileState

    private val _selectedCategory = mutableStateOf("All")
    val selectedCategory: State<String> = _selectedCategory
    fun setCategory(value: String) {
        _selectedCategory.value = value
    }

    private val _productsState = mutableStateOf(ProductsState())
    val productsState: State<ProductsState> = _productsState

    private val _categoriesState = mutableStateOf(emptyList<String>())
    val categoriesState: State<List<String>> = _categoriesState

    private val _bannerImageState =
        mutableStateOf("https://firebasestorage.googleapis.com/v0/b/mealtime-7a501.appspot.com/o/tinywow_Joomia%20Black%20Friday_16608968%20(1).png?alt=media&token=8b874def-e543-482e-80f7-c8cbe9d9f206")
    val bannerImageState: State<String> = _bannerImageState

    private val _searchTerm = mutableStateOf("")
    val searchTerm: State<String> = _searchTerm

    fun setSearchTerm(term: String) {
        _searchTerm.value = term
    }

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val eventFlow: SharedFlow<UiEvents> = _eventFlow.asSharedFlow()

    init {
        getProducts(selectedCategory.value)
        getCategories()
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
            authRepository.getUserProfile().collectLatest { data ->
                val user = gson.fromJson(data, UserResponseDto::class.java)
                _profileState.value = user.toDomain()
            }
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            _categoriesState.value = getCategoriesUseCase()
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

    fun getProducts(category: String = "All", searchTerm: String = "") {
        viewModelScope.launch {
            getProductsUseCase().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        if (category == "All") {
                            _productsState.value = productsState.value.copy(
                                mobiles = if (searchTerm.isEmpty()) {
                                    result.data ?: emptyList()
                                } else {
                                    result.data?.filter {
                                        it.mobileName.contains(
                                            searchTerm,
                                            ignoreCase = true
                                        )
                                    } ?: emptyList()
                                },
                                isLoading = false
                            )
                        } else {
                            _productsState.value = productsState.value.copy(
                                mobiles = result.data?.filter { it.mobileType == category }
                                    ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _productsState.value = productsState.value.copy(
                            isLoading = true
                        )
                    }
                    is Resource.Error -> {
                        _productsState.value = productsState.value.copy(
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

    fun findProducts(searchTerm: String = "") {
        viewModelScope.launch {
            findProductsUseCase(searchTerm).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _productsState.value = productsState.value.copy(
                            mobiles = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        _productsState.value = productsState.value.copy(
                            isLoading = true
                        )
                    }
                    is Resource.Error -> {
                        _productsState.value = productsState.value.copy(
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