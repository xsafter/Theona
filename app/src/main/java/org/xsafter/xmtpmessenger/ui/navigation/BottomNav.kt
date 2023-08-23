@file:OptIn(ExperimentalComposeUiApi::class)

package org.xsafter.xmtpmessenger.ui.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import io.sentry.compose.SentryTraced

@Composable
fun Routing.Main.BottomBar(
    currentRouting: Routing.Main.BottomNav,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    onSelected: (routing: Routing.Main.BottomNav) -> Unit = { }
) {
    SentryTraced("bottom_nav") {
        BottomNavigation(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            //elevation = if (isSystemInDarkTheme()) 0.dp else 4.dp
        ) {
            bottomNavRoutings.forEach { routing ->
                val selected = routing == currentRouting

                BottomNavigationItem(
                    label = { Text(text = routing.label) },
                    icon = { Icon(routing.icon, contentDescription = null) },
                    selected = selected,
                    onClick = { onSelected(routing) },
                    //unselectedContentColor = AmbientContentColor.current.copy(alpha = ContentAlpha.disabled)
                )
            }
        }
    }
}

