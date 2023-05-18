package org.xsafter.xmtpmessenger.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(
        name = "Chats",
        route = "chats",
        icon = Icons.Rounded.Forum,
    ),
    BottomNavItem(
        name = "Map",
        route = "map",
        icon = Icons.Rounded.Map,
    ),
)

@Composable
fun BottomNav(navController: NavController) {

    val backStackEntry = navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            androidx.compose.material3.NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                bottomNavItems.forEach { item ->
                    val selected = item.route == backStackEntry.value?.destination?.route

                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigate(item.route) },
                        label = {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = "${item.name} Icon",
                            )
                        }
                    )
                }
            }
        },
        content = {
            it
        }
    )
}
