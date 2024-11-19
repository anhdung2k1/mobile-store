package com.kanyideveloper.joomia.feature_cart.presentation.checkout

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_cart.domain.model.PayPal.PayPalOrder
import com.kanyideveloper.joomia.feature_cart.domain.model.Transaction
import com.kanyideveloper.joomia.feature_cart.domain.repository.CartRepository
import com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal.CaptureOrderUseCase
import com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal.CreateOrderUseCase
import com.kanyideveloper.joomia.feature_cart.domain.use_case.PayPal.GetPayPalAccessTokenUseCase
import com.kanyideveloper.joomia.feature_profile.data.toDomain
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository,
    private val gson: Gson,
    private val getPayPalAccessTokenUseCase: GetPayPalAccessTokenUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val captureOrderUseCase: CaptureOrderUseCase
) : ViewModel() {

    private val _paymentState = mutableStateOf(PaymentState(paymentItems = emptyList()))
    val paymentState: State<PaymentState> = _paymentState

    private val _isTransactionCreated = mutableStateOf(false)
    val isTransactionCreated: State<Boolean> = _isTransactionCreated

    private val _profileState = mutableStateOf(User())

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val eventFlow: SharedFlow<UiEvents> = _eventFlow.asSharedFlow()

    // PayPal
    private val _payPalAccessToken = MutableStateFlow<String?>(null)
    val payPalAccessToken: StateFlow<String?> get() = _payPalAccessToken

    private val _orderResponse = MutableStateFlow<PayPalOrder?>(null)
    val orderResponse: StateFlow<PayPalOrder?> get() = _orderResponse

    private val _captureResponse = MutableStateFlow<PayPalOrder?>(null)
    val captureResponse: StateFlow<PayPalOrder?> get() = _captureResponse

    init {
        viewModelScope.launch {
            getProfile()
            getAllPayments()
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
    suspend fun createTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _profileState.value.id?.let { userId ->
                _isTransactionCreated.value = cartRepository.createTransaction(userId, transaction)
            }
        }
    }

    private suspend fun getAllPayments() {
        viewModelScope.launch {
            cartRepository.getAllPayments().collectLatest { result ->
                when(result) {
                    is Resource.Success -> {
                        _paymentState.value = paymentState.value.copy(
                            paymentItems = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }

                    is Resource.Loading -> {
                        _paymentState.value = paymentState.value.copy(
                            isLoading = true
                        )
                    }

                    is Resource.Error -> {
                        _paymentState.value = paymentState.value.copy(
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

    // PayPal Functions
    fun getPayPalAccessToken() {
        viewModelScope.launch {
            val token = getPayPalAccessTokenUseCase.execute()
            _payPalAccessToken.value = token.token
        }
    }

    fun createPayPalOrder(amount: String, currency: String) {
        viewModelScope.launch {
            val token = _payPalAccessToken.value ?: return@launch
            val order = createOrderUseCase.execute(token, amount, currency)
            _orderResponse.value = order
        }
    }

    fun capturePayPalOrder(orderId: String) {
        viewModelScope.launch {
            val token = _payPalAccessToken.value ?: return@launch
            val capture = captureOrderUseCase.execute(token, orderId)
            _captureResponse.value = capture
        }
    }
}