package com.kanyideveloper.joomia.feature_cart.presentation.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.core.presentation.ui.theme.MainWhiteColor
import com.kanyideveloper.joomia.core.util.LoadingAnimation
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.CheckOutScreenDestination
import com.kanyideveloper.joomia.feature_cart.domain.model.CartMobile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val shippingFee = 60
@Destination
@Composable
fun CartScreen(
    navigator: DestinationsNavigator,
    viewModel: CartViewModel = hiltViewModel(),
) {

    val state = viewModel.state.value
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvents.SnackbarEvent -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        backgroundColor = Color.White,
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                elevation = 1.dp,
                backgroundColor = Color.White,
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "My Cart",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
            )
        }
    ) {
        CartScreenContent(
            state = state,
            totalPrice = state.cartItems.sumOf { (it.mobilePrice * it.mobileQuantity) + shippingFee },
            onClickCheckOut = { billingPayment ->
              navigator.navigate(CheckOutScreenDestination(billingPayment).route)
            },
            viewModel = viewModel
        )
    }
}

@Composable
private fun CartScreenContent(
    state: CartItemsState,
    totalPrice: Double,
    onClickCheckOut: (Double) -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(state.cartItems) { cartItem ->
                CartItem(
                    cartItem = cartItem,
                    onCartRemoveItem = {
                        coroutineScope.launch {
                            viewModel.updateCartItem(cartItem)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(4.dp),
                )
            }
            item {
                if (state.cartItems.isNotEmpty()) {
                    CheckoutComponent(
                        state = state,
                        totalPrice = totalPrice,
                        onClickCheckOut = { onClickCheckOut(totalPrice) }
                    )
                }
            }
        }

        if (state.isLoading) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingAnimation(
                    circleSize = 16.dp,
                )
            }
        }

        if (state.error != null) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = state.error,
                    color = Color.Red
                )
            }
        }

        if (state.cartItems.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .size(220.dp),
                    painter = painterResource(id = R.drawable.ic_artwork),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun CheckoutComponent(
    state: CartItemsState,
    totalPrice: Double,
    onClickCheckOut: (Double) -> Unit
) {

    Column(Modifier.padding(12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${state.cartItems.size} items")
            Text(
                text = "${state.cartItems.sumOf { (it.mobilePrice * it.mobileQuantity) + shippingFee}}",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Shipping fee")
            Text(
                text = "$${shippingFee}", color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Total")
            Text(
                text = "$${ totalPrice }",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onClickCheckOut(totalPrice) },
            shape = RoundedCornerShape(8)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), text = "Checkout", textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun CartItem(
    cartItem: CartMobile,
    onCartRemoveItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = 3.dp
    ) {
        Row {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = cartItem.imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            placeholder(R.drawable.ic_placeholder)
                            crossfade(true)
                        }).build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                contentScale = ContentScale.Inside
            )
            Spacer(modifier = Modifier.width(5.dp))
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(5.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cartItem.mobileName,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedButton(
                        onClick = { onCartRemoveItem() },
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
                            contentDescription = "Delete Product Item in Cart",
                            tint = MainWhiteColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "$${cartItem.mobilePrice}",
                    color = Color.Black,
                    fontSize = 18.sp,
                    maxLines = 3,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.End,
                    text = "${cartItem.mobileQuantity} Pc",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
