package com.kanyideveloper.joomia.feature_auth.presentation.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanyideveloper.joomia.core.domain.model.TextFieldState
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.feature_auth.domain.use_case.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _usernameState = mutableStateOf(TextFieldState(text = ""))
    val usernameState: State<TextFieldState> = _usernameState
    fun setUsername(value: String) {
        _usernameState.value = _usernameState.value.copy(text = value)
    }

    private val _passwordState = mutableStateOf(TextFieldState(text = ""))
    val passwordState: State<TextFieldState> = _passwordState
    fun setPassword(value : String) {
        _passwordState.value = _passwordState.value.copy(text = value)
    }

    private val _registerState = mutableStateOf(RegisterState())
    val registerState: State<RegisterState> = _registerState

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val evenFlow = _eventFlow.asSharedFlow()

    fun registerUser() {
        viewModelScope.launch {
            _registerState.value = registerState.value.copy(isLoading = true)

            val registerResult = registerUseCase(
                userName = usernameState.value.text,
                password = passwordState.value.text
            )

            _registerState.value = registerState.value.copy(isLoading = false)

            if (registerResult.userNameError != null) {
                _usernameState.value = usernameState.value.copy(error = registerResult.userNameError)
            }

            if (registerResult.passwordError != null) {
                _passwordState.value = passwordState.value.copy(error = registerResult.passwordError)
            }

            when (registerResult.result) {
                is Resource.Success -> {
                    _eventFlow.emit(
                        UiEvents.NavigateEvent(HomeScreenDestination.route)
                    )
                }
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvents.SnackbarEvent(
                            registerResult.result.message ?: "RegisterViewModel(): Unknown error occurred !"
                        )
                    )
                } else -> {
                    _eventFlow.emit(
                        UiEvents.SnackbarEvent(
                            registerResult.result?.message ?: "RegisterViewModel(): Unexpected behaviour"
                        )
                    )
                }
            }
        }
    }
}