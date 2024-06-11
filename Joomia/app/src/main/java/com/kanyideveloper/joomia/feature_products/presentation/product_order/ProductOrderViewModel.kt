package com.kanyideveloper.joomia.feature_products.presentation.product_order

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.destinations.ProductOrderScreenDestination
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_products.domain.model.Order
import com.kanyideveloper.joomia.feature_products.domain.repository.ProductsRepository
import com.kanyideveloper.joomia.feature_profile.data.toDomain
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductOrderViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val authRepository: AuthRepository,
    private val gson: Gson
) : ViewModel(){
    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _isAdminState = mutableStateOf(false)
    val isAdminState: State<Boolean> = _isAdminState

    private val _profileState = mutableStateOf(User())
    private val _state = mutableStateOf(OrderItemsState())
    val state: State<OrderItemsState> = _state

    private val _userState = mutableStateOf(User())
    val userState: State<User> = _userState

    private val _orderState = mutableStateOf(Order(0,0,0,"","",0.0))
    val orderState: State<Order> = _orderState

    init {
        getProfile()
        checkAdminAccount()
        if (_isAdminState.value) {
            getAllOrders()
        } else {
            getAllOrdersByUserID()
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

    private fun checkAdminAccount() {
        viewModelScope.launch {
            _isAdminState.value = _profileState.value.userName?.let {
                authRepository.checkAdminAccount(it)
            } == true
        }
    }

    suspend fun getUserByUserID(userID: Int) {
        viewModelScope.launch {
            authRepository.getUserProfileByUserId(userID).collectLatest { result ->
                when(result) {
                    is Resource.Success -> {
                        _userState.value = result.data ?: User()
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvents.SnackbarEvent(
                            result.message ?: "Unknown error"
                            )
                        )
                    }
                    is Resource.Loading -> {
                        _userState.value = User()
                    }
                }
            }
        }
    }
    suspend fun getOrderByOrderID(orderID: Int) : Order {
        return productsRepository.getOrderByOrderID(orderID)
    }

    suspend fun updateOrder(orderID: Int, orderStatus: String) {
        viewModelScope.launch {
            try {
                Timber.d("updateOrder(): called")
                Timber.d("orderID: $orderID")
                Timber.d("orderStatus: $orderStatus")
                _orderState.value = productsRepository.updateOrder(orderID, orderStatus)
            } catch (e: Exception) {
                _eventFlow.emit(
                    UiEvents.SnackbarEvent(
                        message = "Unknown error occurred"
                    )
                )
            }
        }
    }

    suspend fun deleteOrder(orderID: Int) {
        viewModelScope.launch {
            try {
                Timber.d("deleteOrder(): called")
                Timber.d("orderID: $orderID")
                val isDeleted = productsRepository.deleteOrder(orderID)["deleted"]

                Timber.d("isDeleted: $isDeleted")
                if (isDeleted == true) {
                    _eventFlow.emit(
                        UiEvents.NavigateEvent(
                            ProductOrderScreenDestination.route
                        )
                    )
                    if (_isAdminState.value) {
                        getAllOrders()
                    } else {
                        getAllOrdersByUserID()
                    }
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

    private fun getAllOrdersByUserID() {
        viewModelScope.launch {
            _profileState.value.id?.let {
                productsRepository.getOrdersByUserID(it).collectLatest { result ->
                    when(result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                orderItems = result.data ?: emptyList(),
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
                                isLoading = false
                            )
                            _eventFlow.emit(UiEvents.SnackbarEvent(
                                result.message ?: "Unknown error"
                            ))
                        }
                    }
                }
            }
        }
    }

    private fun getAllOrders() {
        viewModelScope.launch {
            productsRepository.getOrders().collectLatest { result ->
                when(result) {
                    is Resource.Success -> {
                        _state.value = state.value.copy(
                            orderItems = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _state.value = state.value.copy(
                            isLoading = false
                        )
                        _eventFlow.emit(UiEvents.SnackbarEvent(
                            result.message ?: "Unknown error"
                        ))
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }
}
