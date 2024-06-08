package com.kanyideveloper.joomia.feature_profile.presentation.account

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import coil.request.ImageRequest
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
@Destination
fun UserProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val user = viewModel.userProfileState.value.profiles

    var birthDay by remember { mutableStateOf(user?.birthDay) }
    var address by remember { mutableStateOf(user?.address) }
    var gender by remember { mutableStateOf(user?.gender) }

    val genders = listOf("Male", "Female", "Other")
    var selectedImageUri by remember { mutableStateOf<Uri?>(Uri.parse(user?.imageUrl)) }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navigator.popBackStack() },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_left),
                                contentDescription = "Back to Home",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text("User Profile")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        content = { paddingValues ->
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
                    if (user != null) {
                        ProfileImage(
                            imageUri = selectedImageUri ?: user.imageUrl?.let { Uri.parse(it) },
                            onImageClick = { imagePickerLauncher.launch("image/*") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    BirthdayPicker(user?.birthDay) { selectedDate -> birthDay = selectedDate }

                    Spacer(modifier = Modifier.height(16.dp))

                    address?.let { it ->
                        CustomTextField(
                            value = it,
                            onValueChange = { address = it },
                            label = "Address"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GenderDropdownProfile(user?.gender.orEmpty(), genders) { selectedGender -> gender = selectedGender }

                    Spacer(modifier = Modifier.height(24.dp))

                    SaveButton(
                        onClickSave = {
                            coroutineScope.launch {
                                viewModel.updateProfile(
                                    User(
                                        imageUrl = selectedImageUri.toString(),
                                        birthDay = birthDay,
                                        address = address,
                                        gender = gender
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun ProfileImage(imageUri: Uri?, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Image",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.size(40.dp)
            )
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

    var dateText by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(initialDate ?: Date())) }

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
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
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
