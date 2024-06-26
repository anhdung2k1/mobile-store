package com.kanyideveloper.joomia.feature_products.presentation.product_order

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

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

    val orderState = viewModel.orderState.value
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OrderItemDetails(order = orderState)
                Spacer(modifier = Modifier.height(16.dp))
                if (isAdminState) {
                    AdminOrderDetailContent(
                        order = orderState,
                        onUpdateOrderStatus = { status ->
                            coroutineScope.launch {
                                viewModel.updateOrder(orderId, status)
                                viewModel.triggerRefresh()
                            }
                        },
                        onDeleteOrder = {
                            coroutineScope.launch {
                                viewModel.deleteOrder(orderId)
                                viewModel.triggerRefresh()
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun AdminOrderDetailContent(
    order: Order,
    onUpdateOrderStatus: (String) -> Unit,
    onDeleteOrder: () -> Unit
) {
    val statuses = listOf("Success", "Pending", "Processing", "Completed", "Cancelled")
    var selectedStatus by remember { mutableStateOf(order.orderStatus.trim().removeSurrounding("\"")) }

    LaunchedEffect(order.orderStatus) {
        selectedStatus = order.orderStatus.trim().removeSurrounding("\"")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Update Order Status:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            statuses.forEach { status ->
                Timber.d("Selected Status: '$selectedStatus'")
                Timber.d("Status to Compare: '${status.trim()}', Equals Ignore Case: ${selectedStatus.equals(status.trim(), ignoreCase = true)}")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedStatus = status.trim().removeSurrounding("\"")
                            onUpdateOrderStatus(selectedStatus)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedStatus.equals(status.trim(), ignoreCase = true),
                        onClick = {
                            selectedStatus = status.trim().removeSurrounding("\"")
                            onUpdateOrderStatus(selectedStatus)
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
@Composable
fun OrderItemDetails(
    order: Order,
    viewModel: ProductOrderViewModel = hiltViewModel()
) {
    LaunchedEffect(order.userID) {
        viewModel.getUserByUserID(order.userID)
    }

    val userState = viewModel.userState.value

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
