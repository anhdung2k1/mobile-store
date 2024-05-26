package com.kanyideveloper.joomia.feature_auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanyideveloper.joomia.core.domain.model.TextFieldState
import com.kanyideveloper.joomia.core.presentation.ui.theme.YellowMain
import com.kanyideveloper.joomia.core.presentation.ui.theme.poppins
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.ForgotPasswordScreenDestination
import com.kanyideveloper.joomia.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun RegisterScreen(
    navigator: DestinationsNavigator,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val usernameState = viewModel.usernameState.value
    val passwordState = viewModel.passwordState.value

    val registerState = viewModel.registerState.value
    val scaffoldState = rememberScaffoldState()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.evenFlow.collectLatest { event ->
            when (event) {
                is UiEvents.SnackbarEvent -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is UiEvents.NavigateEvent -> {
                    navigator.navigate(
                        event.route
                    )
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Register Successful",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.Top) {
                Text(text = "Getting Started", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Create an account to continue with your shopping",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    ) {
        RegisterScreenContent(
            usernameState = usernameState,
            passwordState = passwordState,
            registerState = registerState,
            onUserNameTextChange = {
                viewModel.setUsername(it)
            },
            onPasswordTextChange = {
                viewModel.setPassword(it)
            },
            onClickForgotPassword = {
                navigator.navigate(ForgotPasswordScreenDestination)
            },
            onClickAlreadyHaveAccount = {
                navigator.popBackStack()
                navigator.navigate(LoginScreenDestination)
            },
            onClickSignUp = {
                keyboardController?.hide()
                viewModel.registerUser()
            }
        )
    }
}

@Composable
private fun RegisterScreenContent(
    usernameState: TextFieldState,
    passwordState: TextFieldState,
    registerState: RegisterState,
    onUserNameTextChange: (String) -> Unit,
    onPasswordTextChange: (String) -> Unit,
    onClickForgotPassword: () -> Unit,
    onClickAlreadyHaveAccount: () -> Unit,
    onClickSignUp: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = usernameState.text,
                onValueChange = {
                    onUserNameTextChange(it)
                },
                label = {
                    Text(text = "User Name")
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Email,
                ),
                maxLines = 1,
                singleLine = true,
                isError = usernameState.error != null
            )
            if (usernameState.error != "") {
                Text(
                    text = usernameState.error ?: "",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = passwordState.text,
                onValueChange = {
                    onPasswordTextChange(it)
                },
                label = {
                    Text(text = "Password")
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Password,
                ),
                maxLines = 1,
                singleLine = true,
                isError = passwordState.error != null
            )
            if (passwordState.error != "") {
                Text(
                    text = passwordState.error ?: "",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onClickSignUp,
                shape = RoundedCornerShape(8),
                enabled = !registerState.isLoading
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp), text = "Sign Up", textAlign = TextAlign.Center
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = onClickSignUp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account?")
                        append(" ")
                        withStyle(
                            style = SpanStyle(color = YellowMain, fontWeight = FontWeight.Bold)
                        ) {
                            append("Sign In")
                        }
                    },
                    fontFamily = poppins,
                    textAlign = TextAlign.Center
                )
            }
        }

        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (registerState.isLoading) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}