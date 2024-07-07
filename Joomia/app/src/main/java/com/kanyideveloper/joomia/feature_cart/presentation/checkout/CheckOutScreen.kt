package com.kanyideveloper.joomia.feature_cart.presentation.checkout

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.kanyideveloper.joomia.destinations.CheckOutScreenDestination
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.feature_cart.domain.model.Payment
import com.kanyideveloper.joomia.feature_cart.domain.model.Transaction
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal

@Destination
@Composable
fun CheckOutScreen(
    billingPayment: Double,
    viewModel: CheckOutViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val paymentMethods = viewModel.paymentState.value

    var selectedPaymentMethod by remember { mutableStateOf<Payment?>(null) }
    val defaultAddress = "123 Street, City, Country"
    var shippingAddress by remember { mutableStateOf(defaultAddress) }
    var showPayPalDialog by remember { mutableStateOf<Boolean?>(false) }

    // PayPal service management
    DisposableEffect(Unit) {
        val intent = Intent(context, PayPalService::class.java).apply {
            putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, viewModel.payPalConfig)
        }
        context.startService(intent)
        onDispose {
            context.stopService(intent)
        }
    }

    // Register for activity result
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
            if (confirm != null) {
                try {
                    val paymentDetails = confirm.toJSONObject().toString(4)
                    Timber.d(paymentDetails)
                    // Process payment details
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // Handle payment cancellation
            Timber.d("Payment Cancel")
        } else if (result.resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            // Handle invalid payment details
            Timber.d("Invalid payment details")
        }
    }

    // Collect payment events and launch PayPal payment
    LaunchedEffect(Unit) {
        viewModel.payPalPaymentEvent.collectLatest { intent ->
            Timber.d("Launching PayPal payment intent")
            launcher.launch(intent)
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 72.dp), contentAlignment = Alignment.Center
                    ) {
                        Text("CHECK OUT", color = Color.Black.copy(alpha = 0.7f), fontWeight = FontWeight.Light)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigate(HomeScreenDestination.route) }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shipping Address
            EditableShippingAddress(
                title = "Shipping Address",
                address = defaultAddress,
                onAddressChanged = {
                    // Handle address change
                    shippingAddress = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods
            Text(
                text = "Select Payment Method",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.Black
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                items(paymentMethods.paymentItems) { method ->
                    PaymentMethodItem(
                        method = method,
                        isSelected = selectedPaymentMethod == method,
                        onMethodSelected = { selectedMethod ->
                            selectedPaymentMethod = selectedMethod
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total")
                Text(
                    text = "$$billingPayment",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Checkout Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        selectedPaymentMethod?.let { paymentMethod ->
                            if (paymentMethod.paymentMethod.contains("Paypal")) {
                                showPayPalDialog = true
                            } else {
                                viewModel.createTransaction(
                                    Transaction(
                                        transactionID = 0,
                                        transactionType = "Sale",
                                        shippingAddress = shippingAddress,
                                        billingPayment = billingPayment,
                                        payment = paymentMethod
                                    )
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(text = "Payment", color = Color.White)
            }

            if (showPayPalDialog == true) {
                if (selectedPaymentMethod?.paymentMethod?.contains("Paypal") == true) {
                    PayPalConfirmationDialog(
                        onConfirm = {
                            showPayPalDialog = false
                            viewModel.initialPayPalPayment(
                                context = context,
                                amount = BigDecimal(billingPayment),
                                currency = "USD",
                                description = "Sample Item"
                            )
                        },
                        onDismiss = {
                            showPayPalDialog = false
                        }
                    )
                }
            }
            if (viewModel.isTransactionCreated.value) {
                PaymentSuccessDialog(
                    onDismiss = {
                        navigator.navigate(CheckOutScreenDestination(billingPayment).route)
                    }
                )
            }
        }
    }
}

@Composable
fun PayPalConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Start PayPal Payment") },
        text = { Text(text = "Do you want to proceed with PayPal payment?") },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = "No")
            }
        }
    )
}

@Composable
fun PaymentSuccessDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(vertical = 24.dp) // Add vertical padding
                    .fillMaxWidth() // Ensure Box fills the width of the dialog
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier
                        .size(120.dp) // Increase the size of the icon
                )
            }
        },
        text = {
            Text(
                text = "Your payment has been processed successfully.",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp) // Add padding
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp) // Add padding
            ) {
                Text(text = "OK", color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun EditableShippingAddress(
    title: String,
    address: String,
    onAddressChanged: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editableAddress by remember { mutableStateOf(address) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isEditing = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isEditing) {
                EditAddressDialog(
                    address = editableAddress,
                    onAddressChanged = {
                        editableAddress = it
                        onAddressChanged(it)
                    },
                    onDismiss = { isEditing = false }
                )
            } else {
                Text(
                    text = editableAddress,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun EditAddressDialog(
    address: String,
    onAddressChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Address") },
        text = {
            TextField(
                value = address,
                onValueChange = onAddressChanged,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "OK")
            }
        }
    )
}

@Composable
fun PaymentMethodItem(
    method: Payment,
    isSelected: Boolean,
    onMethodSelected: (Payment) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .clickable { onMethodSelected(method) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = method.imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            placeholder(R.drawable.ic_placeholder)
                            crossfade(true)
                        }).build()
                ),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = method.paymentMethod,
                fontSize = 16.sp
            )
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(4.dp)
                .background(
                    color = if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    shape = CircleShape
                )
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}
