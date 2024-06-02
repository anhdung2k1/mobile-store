@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.kanyideveloper.joomia.feature_products.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.core.presentation.ui.theme.DarkBlue
import com.kanyideveloper.joomia.core.presentation.ui.theme.MainWhiteColor
import com.kanyideveloper.joomia.core.presentation.ui.theme.YellowMain
import com.kanyideveloper.joomia.core.util.LoadingAnimation
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.ProductDetailsScreenDestination
import com.kanyideveloper.joomia.destinations.ProductSavingScreenDestination
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var filtersExpanded by remember { mutableStateOf(false) }

    val productsState = viewModel.productsState.value
    val categories = viewModel.categoriesState.value
    val user = viewModel.profileState.value
    val isAdminAccount = viewModel.isAdminState.value

    Scaffold(
        topBar = {
            MyTopAppBar(
                currentSearchText = viewModel.searchTerm.value,
                onSearchTextChange = {
                    viewModel.setSearchTerm(it)
                },
                onSearch = {
                    keyboardController?.hide()
                    viewModel.findProducts(
                        searchTerm = viewModel.searchTerm.value
                    )
                },
                onToggleExpand = {
                    filtersExpanded = !filtersExpanded
                },
                user = user
            )
        },
        floatingActionButton = {
            if (isAdminAccount) {
                FloatingActionButton(onClick = {
                    // Navigate to ProductSavingScreen: isUpdate = false -> create new product
                    navigator.navigate(ProductSavingScreenDestination(isUpdate = false))
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        val scaffoldState = rememberScaffoldState()
        LaunchedEffect(key1 = true) {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is UiEvents.SnackbarEvent -> {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                    else -> {}
                }
            }
        }

        DropdownMenu(
            expanded = filtersExpanded,
            offset = DpOffset(x = 200.dp, y = (-600).dp),
            onDismissRequest = {
                filtersExpanded = !filtersExpanded
            }
        ) {
            DropdownMenuItem(
                content = { Text("Iphone") },
                onClick = { viewModel.findProducts("Iphone") }
            )
            DropdownMenuItem(
                content = { Text("Samsung") },
                onClick = { viewModel.findProducts("Samsung") }
            )
            DropdownMenuItem(
                content = { Text("ROG Phone") },
                onClick = { viewModel.findProducts("ROG") }
            )
        }

        HomeScreenContent(
            categories = categories,
            productsState = productsState,
            navigator = navigator,
            bannerImageUrl = viewModel.bannerImageState.value,
            selectedCategory = viewModel.selectedCategory.value,
            onSelectCategory = { category ->
                viewModel.setCategory(category)
                viewModel.getProducts(viewModel.selectedCategory.value)
            },
            viewModel = viewModel
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun HomeScreenContent(
    categories: List<String>,
    productsState: ProductsState,
    navigator: DestinationsNavigator,
    bannerImageUrl: String,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Card(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = bannerImageUrl)
                                .apply(block = fun ImageRequest.Builder.() {
                                    placeholder(R.drawable.ic_placeholder)
                                    crossfade(true)
                                }).build()
                        ),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Black Friday Banner"
                    )
                }

            } // Header for some banner Image

            // Some spacer
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item(span = { GridItemSpan(2) }) {
                Categories(
                    categories = categories,
                    onSelectCategory = onSelectCategory,
                    selectedCategory = selectedCategory
                )
            } // Header with a lazyRow for product categories

            // Some spacer
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Actual product items list
            items(productsState.mobiles) { product ->
                ProductItem(
                    mobile = product,
                    navigator = navigator,
                    onClickAddToCart = {
                        coroutineScope.launch {
                            viewModel.createCartItem(CartMobile(
                                mobileID = product.mobileID,
                                mobileName = product.mobileName,
                                mobilePrice = product.mobilePrice,
                                mobileQuantity = 1,
                                imageUrl = product.imageUrl
                            ))
                        }
                    },
                    modifier = Modifier
                        .width(150.dp)
                )
            }
        }

        if (productsState.isLoading) {
            LoadingAnimation(
                modifier = Modifier.align(Center),
                circleSize = 16.dp,
            )
        }

        if (productsState.error != null) Text(
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Center)
                .padding(16.dp),
            text = productsState.error,
            color = Color.Red
        )
    }
}

@Composable
private fun ProductItem(
    mobile: Mobile,
    onClickAddToCart: () -> Unit,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable {
                navigator.navigate(ProductDetailsScreenDestination(mobile))
            },
        elevation = 2.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = mobile.imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            placeholder(R.drawable.ic_placeholder)
                            crossfade(true)
                        }).build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .align(CenterHorizontally),
                contentScale = ContentScale.Inside
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mobile.mobileName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mobile.mobileType,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    modifier = Modifier
                        .size(18.dp)
                        .align(CenterVertically),
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = null,
                    tint = YellowMain
                )
                Text(
                    modifier = Modifier.align(CenterVertically),
                    text = "${mobile.rating.rate} (${mobile.rating.count})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${mobile.mobilePrice}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = { onClickAddToCart() },
                modifier = Modifier
                    .size(40.dp)
                    .align(End),
                shape = CircleShape,
                border = BorderStroke(0.dp, Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    backgroundColor = YellowMain
                )
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Filled.AddShoppingCart,
                    contentDescription = null,
                    tint = MainWhiteColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

    }
}

@Composable
fun MyTopAppBar(
    currentSearchText: String,
    user: User,
    onSearchTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    onToggleExpand: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = "https://mobile-bucket.s3.amazonaws.com/mobile_images/avatar.jpg")
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(35.dp),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Hi, ${user.userName}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_allert),
                contentDescription = null,
                tint = DarkBlue
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = CenterVertically
        ) {
            TextField(
                value = currentSearchText,
                onValueChange = {
                    onSearchTextChange(it)
                },
                placeholder = {
                    Text(
                        text = "Search",
                    )
                },

                modifier = Modifier
                    .fillMaxWidth(0.80f)
                    .background(MainWhiteColor, shape = RoundedCornerShape(8.dp))
                    .clickable {

                    },
                shape = RoundedCornerShape(size = 8.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch()
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    disabledTextColor = MainWhiteColor,
                    backgroundColor = MainWhiteColor,
                    focusedIndicatorColor = MainWhiteColor,
                    unfocusedIndicatorColor = MainWhiteColor,
                    disabledIndicatorColor = MainWhiteColor
                ),
                textStyle = TextStyle(color = Color.Black),
                maxLines = 1,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = DarkBlue
                    )
                }
            )

            IconButton(onClick = onToggleExpand) {
                Icon(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(
                            shape = RoundedCornerShape(
                                size = 8.dp
                            )
                        )
                        .background(
                            MainWhiteColor
                        )
                        .padding(
                            start = 4.dp,
                            end = 4.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = null,
                    tint = DarkBlue
                )
            }
        }
    }
}


@Composable
fun Categories(
    categories: List<String>,
    onSelectCategory: (String) -> Unit,
    selectedCategory: String,
) {
    LazyRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories) { category ->
            Text(
                text = category,
                style = typography.body1.merge(),
                color = Color.Black,
                modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            size = 8.dp,
                        ),
                    )
                    .clickable {
                        onSelectCategory(category)
                    }
                    .background(
                        if (category == selectedCategory) {
                            YellowMain
                        } else {
                            MainWhiteColor
                        }
                    )
                    .padding(
                        10.dp
                    )
            )
        }
    }
}