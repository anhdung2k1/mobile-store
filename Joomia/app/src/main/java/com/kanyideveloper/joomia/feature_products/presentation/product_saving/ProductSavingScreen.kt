package com.kanyideveloper.joomia.feature_products.presentation.product_saving

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.core.presentation.ui.theme.YellowMain
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.AccountScreenDestination
import com.kanyideveloper.joomia.destinations.CartScreenDestination
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun ProductSavingScreen(
    navigator: DestinationsNavigator,
    isUpdate: Boolean = false,
    mobileID: Int = 0,
    viewModel: ProductSavingViewModel = hiltViewModel()
) {
    var productName by remember { mutableStateOf("") }
    var productModel by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    val types = viewModel.categoriesState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 72.dp), contentAlignment = Alignment.Center) {
                            Text("NEW PRODUCT", color = Color.Black.copy(alpha = 0.7f), fontWeight = FontWeight.Light)
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
        }
    ) {
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

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item(span = { GridItemSpan(2) }) {
                    ProductAddingForm(
                        productName = productName,
                        onProductNameChange = { productName = it },
                        productModel = productModel,
                        onProductModelChange = { productModel = it },
                        productType = productType,
                        onProductTypeChange = { productType = it },
                        productDescription = productDescription,
                        onProductDescriptionChange = { productDescription = it },
                        productPrice = productPrice,
                        onProductPriceChange = { productPrice = it },
                        productQuantity = productQuantity,
                        onProductQuantityChange = { productQuantity = it },
                        typeExpanded = typeExpanded,
                        onTypeExpandedChange = { typeExpanded = it },
                        modelExpanded = modelExpanded,
                        onModelExpandedChange = { modelExpanded = it },
                        types = types,
                        selectedImageUri = selectedImageUri,
                        onSelectImageClick = { imagePickerLauncher.launch("image/*") },
                        onClickSaveProduct = {
                            viewModel.saveProduct(
                                context,
                                mobileID,
                                productName,
                                productModel,
                                productType,
                                productDescription,
                                productPrice,
                                productQuantity,
                                selectedImageUri,
                                isUpdate = isUpdate
                            )}
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProductAddingForm(
    productName: String,
    onProductNameChange: (String) -> Unit,
    productModel: String,
    onProductModelChange: (String) -> Unit,
    productType: String,
    onProductTypeChange: (String) -> Unit,
    productDescription: String,
    onProductDescriptionChange: (String) -> Unit,
    productPrice: String,
    onProductPriceChange: (String) -> Unit,
    productQuantity: String,
    onProductQuantityChange: (String) -> Unit,
    typeExpanded: Boolean,
    onTypeExpandedChange: (Boolean) -> Unit,
    modelExpanded: Boolean,
    onModelExpandedChange: (Boolean) -> Unit,
    types: List<String>,
    selectedImageUri: Uri?,
    onSelectImageClick: () -> Unit,
    onClickSaveProduct: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val models = listOf("Android", "IOS")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProductImagePicker(selectedImageUri, onSelectImageClick)

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = productName,
            onValueChange = onProductNameChange,
            label = { Text("Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AddShoppingCart,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "Product Model",
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable { onModelExpandedChange(true) } // Make the entire Row clickable
                ) {
                    BasicTextField(
                        value = productModel,
                        onValueChange = onProductModelChange,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.body1,
                        singleLine = true,
                        readOnly = true
                    )

                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = modelExpanded,
                onDismissRequest = { onModelExpandedChange(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                models.forEach { type ->
                    DropdownMenuItem(onClick = {
                        onProductModelChange(type)
                        onModelExpandedChange(false)
                    }) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.body1,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "Product Type",
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable { onTypeExpandedChange(true) } // Make the entire Row clickable
                ) {
                    BasicTextField(
                        value = productType,
                        onValueChange = onProductTypeChange,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.body1,
                        singleLine = true,
                        readOnly = true
                    )

                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { onTypeExpandedChange(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                types.forEach { type ->
                    DropdownMenuItem(onClick = {
                        onProductTypeChange(type)
                        onTypeExpandedChange(false)
                    }) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.body1,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = productDescription,
            onValueChange = onProductDescriptionChange,
            label = { Text("Product Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        OutlinedTextField(
            value = productPrice,
            onValueChange = onProductPriceChange,
            label = { Text("Product Price") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.PriceChange,
                    contentDescription = null
                )
            }
        )

        OutlinedTextField(
            value = productQuantity,
            onValueChange = onProductQuantityChange,
            label = { Text("Product Quantity") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.ProductionQuantityLimits,
                    contentDescription = null
                )
            }
        )

        Button(
            onClick = { onClickSaveProduct() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = YellowMain,
                contentColor = Color.White
            )
        ) {
            Text("Save Product")
        }
    }
}

@Composable
fun ProductImagePicker(
    selectedImageUri: Uri?,
    onSelectImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray) // Set the inner color to light gray
            .drawBehind {
                val borderWidth = 4.dp.toPx()
                val dashWidth = 10.dp.toPx()
                val dashGap = 10.dp.toPx()
                drawRoundRect(
                    color = Color.Gray,
                    topLeft = Offset.Zero,
                    size = size,
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Stroke(
                        width = borderWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap))
                    )
                )
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.align(Alignment.Center),
                    tint = MaterialTheme.colors.onSurface
                )
            }
            Button(
                onClick = onSelectImageClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Text("Select Image")
            }
        }
    }
}