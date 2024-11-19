package com.kanyideveloper.joomia.feature_profile.presentation.account

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kanyideveloper.joomia.core.util.Resource
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.AccountScreenDestination
import com.kanyideveloper.joomia.destinations.AuthDashboardScreenDestination
import com.kanyideveloper.joomia.feature_auth.data.dto.UserResponseDto
import com.kanyideveloper.joomia.feature_auth.data.local.AuthPreferences
import com.kanyideveloper.joomia.feature_auth.domain.repository.AuthRepository
import com.kanyideveloper.joomia.feature_auth.domain.use_case.LogoutUseCase
import com.kanyideveloper.joomia.feature_profile.data.toDomain
import com.kanyideveloper.joomia.feature_profile.data.toDto
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase,
    private val gson: Gson
) : ViewModel() {

    private val _profileState = mutableStateOf(ProfileState())
    private val profileState: State<ProfileState> = _profileState

    private val _accountIDState = mutableStateOf(0)

    private val _isAccountPasswordState = mutableStateOf(false)
    val isAccountPasswordState: State<Boolean> = _isAccountPasswordState

    private val _userProfileState = mutableStateOf(ProfileState())
    val userProfileState: State<ProfileState> = _userProfileState

    private val _eventFlow = MutableSharedFlow<UiEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getProfile()
        getProfileByUserId()
        getAccountIDByUserName()
    }

    private fun getProfile() {
        viewModelScope.launch {
            authRepository.getUserProfile().collectLatest { data ->
                Timber.d("Data: $data")
                val user = gson.fromJson(data, UserResponseDto::class.java)
                _profileState.value = profileState.value.copy(profiles = user.toDomain())
            }
        }
    }

    private fun getProfileByUserId() {
        viewModelScope.launch {
            _profileState.value.profiles?.id?.let { id ->
                authRepository.getUserProfileByUserId(id).collectLatest { result ->
                    when(result) {
                        is Resource.Success -> {
                            _userProfileState.value = userProfileState.value.copy(
                                profiles = result.data,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            _userProfileState.value = userProfileState.value.copy(isLoading = true)
                        }
                        is Resource.Error -> {
                            _userProfileState.value = userProfileState.value.copy(
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

    private fun getAccountIDByUserName() {
        viewModelScope.launch {
            _profileState.value.profiles?.userName?.let { userName ->
                _accountIDState.value = authRepository.getAccountIdByUserName(userName)
                Timber.d("AccountID: ${_accountIDState.value}")
            }
        }
    }

    suspend fun updateAccountPassword(password: String) {
        viewModelScope.launch {
            _accountIDState.value.let {
                Timber.d("updateAccountPassword(): $it, $password")
                _isAccountPasswordState.value = authRepository.updateAccountPassword(it, password)
            }
        }
    }

    suspend fun updateProfile(user: User) {
        viewModelScope.launch {
            _profileState.value.profiles?.id?.let { id ->
                authRepository.updateUser(id, user).collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _userProfileState.value = userProfileState.value.copy(
                                profiles = result.data,
                                isLoading = false
                            )
                            _eventFlow.emit(
                                UiEvents.SnackbarEvent(
                                    message = "Profile updated successfully!"
                                )
                            )
                            _eventFlow.emit(
                                UiEvents.NavigateEvent(
                                    route = AccountScreenDestination.route
                                )
                            )
                            Timber.d("Profile updated successfully!")
                            _profileState.value.profiles?.id?.let { id ->
                                authRepository.getUserProfileByUserId(id).collectLatest {result ->
                                    when(result) {
                                        is Resource.Success -> {
                                            result.data?.toDto()
                                                ?.let { authPreferences.saveUserdata(it) }
                                        }
                                        else -> {
                                            Timber.d("Error: ${result.message}")
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

                        is Resource.Loading -> {
                            _userProfileState.value = userProfileState.value.copy(isLoading = true)
                        }

                        is Resource.Error -> {
                            _userProfileState.value = userProfileState.value.copy(
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

    fun logout() {
        viewModelScope.launch {
            val result = logoutUseCase()
            Timber.d("Result: ${result.message}")
            when (result) {
                is Resource.Success -> {
                    _eventFlow.emit(
                        UiEvents.NavigateEvent(route = AuthDashboardScreenDestination.route)
                    )
                }

                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvents.SnackbarEvent(
                            message = result.message ?: "Unknown error occurred"
                        )
                    )
                }

                else -> {
                    _eventFlow.emit(
                        UiEvents.SnackbarEvent(
                            message = "Unknown error occurred"
                        )
                    )
                }
            }
        }
    }
}
