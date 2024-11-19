package com.kanyideveloper.joomia.feature_profile.presentation.account

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.Utils.Utils
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.AccountScreenDestination
import com.kanyideveloper.joomia.destinations.CartScreenDestination
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
@Destination
fun UserProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current

    val userState = viewModel.userProfileState.value
    val user = userState.profiles

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var birthDay by remember { mutableStateOf<Date?>(null) }
    var address by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        birthDay = user?.birthDay?.let {
            try {
                dateFormat.parse(it)
            } catch (e: ParseException) {
                Timber.e(e, "Failed to parse birthDay: $it")
                null
            }
        }
        address = user?.address.orEmpty()
        gender = user?.gender.orEmpty()
        imageUrl = user?.imageUrl.orEmpty()
    }
    Timber.d("UserProfileScreen: $user")

    val genders = listOf("Male", "Female", "Other")

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 72.dp), contentAlignment = Alignment.Center
                    ) {
                        Text("USER PROFILE", color = Color.Black.copy(alpha = 0.7f), fontWeight = FontWeight.Light)
                    }
                },
                backgroundColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_left),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        },
        content = { paddingValues ->

            val scaffoldState = rememberScaffoldState()

            LaunchedEffect(key1 = true) {
                viewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        is UiEvents.SnackbarEvent -> {
                            event.message.let {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = it,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                        is UiEvents.NavigateEvent -> {
                            navigator.navigate(event.route) {
                                popUpTo(AccountScreenDestination.route) {
                                    inclusive = false
                                }
                                popUpTo(HomeScreenDestination.route) {
                                    inclusive = false
                                }
                                popUpTo(CartScreenDestination.route) {
                                    inclusive = false
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colors.background),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    user?.let {
                        ProfileImage(
                            selectedImageUri = selectedImageUri,
                            imageUrl = imageUrl,
                            onSelectImageClick = { imagePickerLauncher.launch("image/*") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    GenderDropdownProfile(gender, genders) { selectedGender -> gender = selectedGender }

                    Spacer(modifier = Modifier.height(24.dp))

                    BirthdayPicker(birthDay) { birthDay = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Address"
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SaveButton(
                        onClickSave = {
                            coroutineScope.launch {
                                val formattedBirthDay = birthDay?.let { dateFormat.format(it) }
                                val updatedImageUrl = selectedImageUri?.let {
                                    Utils.convertImageToBase64(context, it)
                                } ?: imageUrl

                                if (updatedImageUrl == imageUrl) {
                                    viewModel.updateProfile(
                                        User(
                                            birthDay = formattedBirthDay,
                                            address = address,
                                            gender = gender
                                        )
                                    )
                                }
                                else {
                                    viewModel.updateProfile(
                                        User(
                                            imageUrl = updatedImageUrl,
                                            birthDay = formattedBirthDay,
                                            address = address,
                                            gender = gender
                                        )
                                    )
                                }
                                if (password.isNotEmpty()) {
                                    viewModel.updateAccountPassword(password)
                                    if (viewModel.isAccountPasswordState.value) {
                                        Timber.d("Password updated successfully")
                                        navigator.navigate(HomeScreenDestination.route)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    )
}
@Composable
fun ProfileImage(
    selectedImageUri: Uri?,
    imageUrl: String,
    onSelectImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(160.dp) // Set both width and height to 80 dp
            .background(
                Color.LightGray,
                shape = CircleShape
            ) // Set the inner color to light gray and shape to circle
            .clickable { onSelectImageClick() }
            .drawBehind {
                val borderWidth = 4.dp.toPx()
                val dashWidth = 10.dp.toPx()
                val dashGap = 10.dp.toPx()
                drawCircle(
                    color = Color.Gray,
                    radius = size.minDimension / 2,
                    style = Stroke(
                        width = borderWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap))
                    )
                )
            },
        elevation = 4.dp,
        shape = CircleShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun BirthdayPicker(initialDate: Date?, onDateSelected: (Date) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    initialDate?.let { calendar.time = it }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var dateText by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)) }

    OutlinedTextField(
        value = dateText,
        onValueChange = { },
        readOnly = true,
        label = { Text("Birthday") },
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        calendar.set(selectedYear, selectedMonth, selectedDay)
                        val selectedDate = calendar.time
                        dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
                        onDateSelected(selectedDate)
                    },
                    year, month, day
                ).show()
            }) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun GenderDropdownProfile(selectedGender: String, genders: List<String>, onGenderSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = selectedGender.ifEmpty { "Select Gender" }, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(onClick = {
                    onGenderSelected(gender)
                    expanded = false
                }) {
                    Text(text = gender, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false) {
    var isEditable by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (isEditable) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        placeholder = { Text(label) }, // Placeholder text
        modifier = Modifier.fillMaxWidth(),
        readOnly = !isEditable,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            Row {
                if (isPassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier
                            .clickable { passwordVisible = !passwordVisible }
                            .padding(end = 8.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .clickable { isEditable = !isEditable }
                        .padding(end = 8.dp)
                )
            }
        }
    )
}
@Composable
fun SaveButton(
    onClickSave: () -> Unit
) {
    Button(
        onClick = {
            // Save user profile changes
            onClickSave()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.primary),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White
        )
    ) {
        Text("Save", color = Color.White)
    }
}
