package com.kanyideveloper.joomia.feature_cart.presentation.checkout

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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanyideveloper.joomia.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CheckOutScreen(
    navigator: DestinationsNavigator
) {
    val scaffoldState = rememberScaffoldState()
    val paymentMethods = listOf(
        PaymentMethod("Credit Card", R.drawable.master_card),
        PaymentMethod("PayPal", R.drawable.paypal),
        PaymentMethod("Cash", R.drawable.cash),
        PaymentMethod("MoMo", R.drawable.momo)
    )

    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var shippingAddress by remember { mutableStateOf("") }

    Scaffold(
        backgroundColor = Color.White,
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                elevation = 1.dp,
                backgroundColor = Color.White,
                title = {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 72.dp), contentAlignment = Alignment.Center) {
                        Text("CHECK OUT", color = Color.Black.copy(alpha = 0.7f), fontWeight = FontWeight.Light)
                    }
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
                address = "123 Street, City, Country",
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
                items(paymentMethods) { method ->
                    PaymentMethodItem(
                        method = method,
                        isSelected = selectedPaymentMethod == method,
                        onMethodSelected = { selectedMethod ->
                            selectedPaymentMethod = selectedMethod
                        }
                    )
                }
            }

            // Checkout Button
            Button(
                onClick = { /* Handle checkout action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(text = "Checkout", color = Color.White)
            }
        }
    }
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
    method: PaymentMethod,
    isSelected: Boolean,
    onMethodSelected: (PaymentMethod) -> Unit
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
                painter = painterResource(id = method.icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = method.name,
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
data class PaymentMethod(val name: String, val icon: Int)