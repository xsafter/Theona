package org.xsafter.xmtpmessenger.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Map
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Routing(val route: String, val label: String) {

    object Main : Routing(route = "main", label = "Main") {

        val bottomNavRoutings = listOf(BottomNav.Chats, BottomNav.Map)

        sealed class BottomNav(
            route: String,
            label: String,
            val icon: ImageVector,
        ): Routing(route, label) {
            object Chats : BottomNav(
                route = "chats",
                label = "Chats",
                Icons.Rounded.Forum,
            )

            object Map : BottomNav(
                route = "map",
                label = "Map",
                Icons.Rounded.Map,
            )
        }
    }

    object Conversation : Routing(route = "conversation", label = "Conversation") {
        object Info

    }

    object Search : Routing(route = "search", label = "Search")
    object Settings : Routing(route = "settings", label = "Settings")
}
