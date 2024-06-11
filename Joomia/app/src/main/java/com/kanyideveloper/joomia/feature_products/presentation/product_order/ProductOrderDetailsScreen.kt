package com.kanyideveloper.joomia.feature_products.presentation.product_order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.feature_products.domain.model.Order
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProductOrderDetailsScreen(
    orderId: Int,
    viewModel: ProductOrderViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    LaunchedEffect(key1 = true) {
        viewModel.getOrderByOrderID(orderId)
    }

    val state = viewModel.state.value
    val isAdminState = viewModel.isAdminState.value
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
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
                        text = "Order Detail",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
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
        content = {
            LaunchedEffect(key1 = true) {
                viewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        is UiEvents.SnackbarEvent -> {
                            scaffoldState.snackbarHostState.showSnackbar(event.message)
                        }
                        else -> {
                            navigator.navigateUp()
                        }
                    }
                }
            }
            if (isAdminState) {
                AdminOrderDetailContent(
                    state = state,
                    onUpdateOrderStatus = { status ->
                        coroutineScope.launch {
                            viewModel.updateOrder(orderId, status)
                        }
                    },
                    onDeleteOrder = {
                        coroutineScope.launch {
                            viewModel.deleteOrder(orderId)
                        }
                    },
                    viewModel = viewModel
                )
            }
        }
    )
}

@Composable
fun AdminOrderDetailContent(
    state: OrderItemsState,
    onUpdateOrderStatus: (String) -> Unit,
    onDeleteOrder: () -> Unit,
    viewModel: ProductOrderViewModel = hiltViewModel()
) {
    if (state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else if (state.error != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = state.error, color = Color.Red)
        }
    } else {
        state.orderItems.firstOrNull()?.let { order ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    OrderItemDetails(
                        order = order,
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Update Order Status:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    val statuses = listOf("Pending", "Processing", "Completed", "Cancelled")
                    var selectedStatus by remember { mutableStateOf(order.orderStatus) }
                    statuses.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStatus = status
                                    onUpdateOrderStatus(status)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = {
                                    selectedStatus = status
                                    onUpdateOrderStatus(status)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = status)
                        }
                    }
                }
                Button(
                    onClick = onDeleteOrder,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Delete Order", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun OrderItemDetails(
    order: Order,
    viewModel: ProductOrderViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.getUserByUserID(order.userID)
    }

    val userState = viewModel.userState.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Order #${order.orderID}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "$${order.totalAmount}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Text(
            text = order.orderStatus,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = "https://mobile-bucket.s3.amazonaws.com/mobile_images/orderIcon.png")
                    .apply {
                        placeholder(R.drawable.ic_placeholder)
                        crossfade(true)
                    }.build()
            ),
            contentDescription = "Image Order",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Customer: ${userState.userName}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Address: ${userState.address}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
