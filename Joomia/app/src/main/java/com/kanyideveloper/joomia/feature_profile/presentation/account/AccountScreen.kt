package com.kanyideveloper.joomia.feature_profile.presentation.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.core.presentation.ui.theme.GrayColor
import com.kanyideveloper.joomia.core.presentation.ui.theme.YellowMain
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.AccountScreenDestination
import com.kanyideveloper.joomia.destinations.CartScreenDestination
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.destinations.ProductOrderScreenDestination
import com.kanyideveloper.joomia.destinations.UserProfileScreenDestination
import com.kanyideveloper.joomia.destinations.WishlistScreenDestination
import com.kanyideveloper.joomia.feature_products.presentation.product_order.orderCount
import com.kanyideveloper.joomia.feature_profile.domain.model.User
import com.kanyideveloper.joomia.feature_wishlist.presentation.wishListCount
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Destination
@Composable
fun AccountScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val user = viewModel.userProfileState.value

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
                        text = "My Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) {
        user.profiles?.let { it1 ->
            AccountScreenContent(
                user = it1,
                onClickAvatar = {
                    navigator.navigate(UserProfileScreenDestination.route)
                },
                onClickSignOut = {
                    viewModel.logout()
                },
                navigator = navigator
            )
        }
    }
}

@Composable
private fun AccountScreenContent(
    user: User,
    onClickAvatar: () -> Unit,
    onClickSignOut: () -> Unit,
    navigator: DestinationsNavigator,
) {
    LazyColumn {
        item {
            UserItem(
                user = user,
                onClickAvatar = onClickAvatar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(4.dp)
            )
        }

        item {
            CardAccount(
                title = "My Orders",
                content = "You have $orderCount completed orders"
            ) {
                navigator.navigate(ProductOrderScreenDestination.route)
            }

            CardAccount(
                title = "My WishLists",
                content = "You have $wishListCount in wishlists"
            ) {
                navigator.navigate(WishlistScreenDestination.route)
            }

            CardAccount(
                title = "User Profile",
                content = "Manage your account settings"
            ) {
                navigator.navigate(UserProfileScreenDestination.route)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = onClickSignOut,
                shape = RoundedCornerShape(8)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    text = "Sign Out",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CardAccount(
    title: String,
    content: String,
    onClickAccount: () -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        border = BorderStroke(0.3.dp, GrayColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content,
                    color = Color.Black,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = { onClickAccount() }) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onClickAvatar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = 3.dp
    ) {
        Row {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .clickable { onClickAvatar() }
                    .fillMaxHeight()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = user.imageUrl?.ifEmpty { R.drawable.ic_user } )
                            .apply(block = fun ImageRequest.Builder.() {
                                placeholder(R.drawable.ic_placeholder)
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Inside
                )
            }
            Spacer(modifier = Modifier.width(5.dp))

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(5.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "${
                        user.userName?.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    }",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "@${user.userName}",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 3,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.align(End),
                    onClick = {
                        onClickAvatar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        backgroundColor = YellowMain
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(3.dp),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        text = "Edit profile"
                    )
                }
            }
        }
    }
}