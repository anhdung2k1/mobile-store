package com.kanyideveloper.joomia.feature_products.domain.model

import com.kanyideveloper.joomia.R
import com.kanyideveloper.joomia.destinations.*

sealed class BottomNavItem(var icon: Int, var destination: Destination) {
    object Home : BottomNavItem(
        icon = R.drawable.ic_home,
        destination = HomeScreenDestination
    )

    object WishList : BottomNavItem(
        icon = R.drawable.ic_heart,
        destination = WishlistScreenDestination
    )

    object Order : BottomNavItem(
        icon = R.drawable.ic_star,
        destination = ProductOrderScreenDestination
    )

    object Cart : BottomNavItem(
        icon = R.drawable.ic_basket,
        destination = CartScreenDestination
    )

    object Account : BottomNavItem(
        icon = R.drawable.ic_user,
        destination = AccountScreenDestination
    )
}
