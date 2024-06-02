package com.kanyideveloper.joomia.feature_products.presentation.product_details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.core.presentation.ui.theme.GrayColor
import com.kanyideveloper.joomia.core.presentation.ui.theme.MainWhiteColor
import com.kanyideveloper.joomia.core.presentation.ui.theme.YellowMain
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.ProductSavingScreenDestination
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.kanyideveloper.joomia.feature_products.domain.model.Mobile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProductDetailsScreen(
    mobile: Mobile,
    navigator: DestinationsNavigator,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {

    val isAdmin = viewModel.isAdminState.value
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        backgroundColor = Color.White,
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navigator.popBackStack()
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_left),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_heart),
                        tint = GrayColor,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
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
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                    is UiEvents.NavigateEvent -> {
                        navigator.navigate(
                            event.route
                        )
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "Delete Product Successful",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }

        DetailsScreenContent(
            mobile = mobile,
            modifier = Modifier.fillMaxSize(),
            isAdmin = isAdmin,
            onProductUpdateClick = { navigator.navigate(ProductSavingScreenDestination(isUpdate = true, mobileID = mobile.mobileID)) },
            onProductDeleteClick = { viewModel.deleteProduct(mobile.mobileID) },
            onClickAddToCart = {
                coroutineScope.launch {
                    viewModel.createCartItem(
                        CartMobile(
                            mobileID = mobile.mobileID,
                            mobileName = mobile.mobileName,
                            mobilePrice = mobile.mobilePrice,
                            mobileQuantity = 1,
                            imageUrl = mobile.imageUrl
                        )
                    )
            } }
        )
    }
}

@Composable
fun DetailsScreenContent(
    mobile: Mobile,
    modifier: Modifier = Modifier,
    isAdmin: Boolean,
    onProductUpdateClick: () -> Unit,
    onProductDeleteClick: () -> Unit,
    onClickAddToCart: () -> Unit
) {
    Column {
        Box(modifier = modifier.weight(1f), contentAlignment = Alignment.Center) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = mobile.imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                            placeholder(R.drawable.ic_placeholder)
                        }).build()
                ),
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Inside
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = modifier
                .fillMaxWidth()
                .weight(2f),
            elevation = 0.dp,
            shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp),
            backgroundColor = MainWhiteColor
        ) {

            Box(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mobile.mobileName,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )

                        if (isAdmin) {
                            OutlinedButton(
                                onClick = { onProductUpdateClick() },
                                modifier = Modifier.size(40.dp),
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
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Update Product",
                                    tint = MainWhiteColor
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            OutlinedButton(
                                onClick = { onProductDeleteClick() },
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                border = BorderStroke(0.dp, Color.Transparent),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White,
                                    backgroundColor = Color.Red
                                )
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete Product",
                                    tint = MainWhiteColor
                                )
                            }
                        }
                    }

                    Text(
                        text = mobile.mobileType,
                        color = Color.Black,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                    )

                    Text(
                        text = mobile.mobileModel,
                        color = Color.Black,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val rating: Float by remember { mutableStateOf(mobile.rating.rate.toFloat()) }

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBar(
                            value = rating,
                            config = RatingBarConfig()
                                .activeColor(YellowMain)
                                .inactiveColor(GrayColor)
                                .stepSize(StepSize.HALF)
                                .numStars(5)
                                .isIndicator(true)
                                .size(16.dp)
                                .padding(3.dp)
                                .style(RatingBarStyle.HighLighted),
                            onValueChange = {},
                            onRatingChanged = {}
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${mobile.rating.count})",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = mobile.mobileDescription,
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )

                }
                Row(
                    modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$${mobile.mobilePrice}",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = { onClickAddToCart() },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            backgroundColor = YellowMain,
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.add_to_cart)
                        )
                    }
                }
            }
        }
    }
}