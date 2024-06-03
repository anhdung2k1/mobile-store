package com.kanyideveloper.joomia.feature_wishlist.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.core.presentation.ui.theme.YellowMain
import com.kanyideveloper.joomia.core.util.UiEvents
import com.kanyideveloper.joomia.destinations.ProductDetailsScreenDestination
import com.kanyideveloper.joomia.destinations.WishlistScreenDestination
import com.kanyideveloper.joomia.feature_wishlist.domain.model.WishListMobile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@Composable
fun WishlistScreen (
    navigator: DestinationsNavigator,
    viewModel: WishListViewModel = hiltViewModel(),
) {

    val state = viewModel.state.value
    val user = viewModel.profilState.value
    val scaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.evenFlow.collectLatest { event ->
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
                            .fillMaxWidth()
                            .padding(end = 24.dp),
                        text = "Wishlist",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                user.id?.let { viewModel.removeAllWishListItems(it) }
                                navigator.navigate(WishlistScreenDestination.route)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) {
        WishListScreenContent(
            wishlistItems = state,
            onClickOneWishItem = {
                wishlist ->
                coroutineScope.launch {
                    navigator.navigate(ProductDetailsScreenDestination(
                        viewModel.getMobileDevice(wishlist.mobileID)
                    ))
                }
            },
            onClickWishIcon = { wishlist ->
                coroutineScope.launch {
                    viewModel.updateWishListItem(wishlist)
                }
            }
        )
    }
}

@Composable
private fun WishListScreenContent(
    wishlistItems: WishListItemsState,
    onClickOneWishItem: (WishListMobile) -> Unit,
    onClickWishIcon: (WishListMobile) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(wishlistItems.wishListItems) { wishlist ->
                WishlistItem(
                    wishlist = wishlist, modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp)
                        .padding(8.dp),
                    onClickOneWishItem = onClickOneWishItem,
                    onClickWishIcon = onClickWishIcon,
                )
            }
        }

        if (wishlistItems.wishListItems.isEmpty()) {
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WishlistItem(
    wishlist: WishListMobile,
    modifier: Modifier = Modifier,
    onClickOneWishItem: (WishListMobile) -> Unit,
    onClickWishIcon: (WishListMobile) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = 3.dp,
        onClick = {
            onClickOneWishItem(wishlist)
        }
    ) {
        Row {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = wishlist.imageUrl)
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
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(5.dp)
            ) {
                Text(
                    text = wishlist.mobileName,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "$${wishlist.mobilePrice}",
                    color = Color.Black,
                    fontSize = 22.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Light
                )
                IconButton(
                    onClick = {
                        onClickWishIcon(wishlist)
                    },
                    modifier = Modifier.align(End),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_heart_fill),
                        tint = YellowMain,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}