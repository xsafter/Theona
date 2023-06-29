package org.xsafter.xmtpmessenger.activities

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.ClientManager
import org.xsafter.xmtpmessenger.activities.viewmodels.MapViewModel
import org.xsafter.xmtpmessenger.activities.viewmodels.UsersViewModel
import org.xsafter.xmtpmessenger.data.GeoMessageWrapper
import org.xsafter.xmtpmessenger.data.User
import org.xsafter.xmtpmessenger.ui.BottomBar
import org.xsafter.xmtpmessenger.ui.Routing
import org.xsafter.xmtpmessenger.ui.components.Content


@Composable
fun Routing.Main.Content(
    client: Client,
    usersViewModel: UsersViewModel,
    mapViewModel: MapViewModel = MapViewModel(client),
    onChatClick: (user: User) -> Unit,
    onSearchClick: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    fun currentRouting(): Routing.Main.BottomNav? {
        val currentRoute = navBackStackEntry?.destination?.route
        return bottomNavRoutings.find { it.route == currentRoute }
    }

    val users: List<User> by usersViewModel.users.collectAsState()

    Log.e("users_list", users.joinToString())

    if (users.isNotEmpty())
        mapViewModel.setupGeoConversations(users.map{ it.username }.toMutableList())

    val geoMessages: List<GeoMessageWrapper> by mapViewModel.geoMessages.collectAsState()


    Box {
        Scaffold(
            topBar = {

            },
            bottomBar = {
                currentRouting()?.let {
                    BottomBar(
                        currentRouting = it,
                        onSelected = { selectedRouting ->
                            if (it.route != selectedRouting.route) {
                                if(selectedRouting is Routing.Main.BottomNav.Chats)
                                    bottomNavController.popBackStack(bottomNavController.graph.startDestinationId, false)
                                else
                                    bottomNavController.navigate(selectedRouting.route)
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.surface
            ) {

                val context = LocalContext.current
                // TODO: fix BottomNav state when navigating back from another screen (like navigating back from Conversation to Main)
                NavHost(
                    navController = bottomNavController,
                    startDestination = Routing.Main.BottomNav.Chats.route
                ) {
                    composable(Routing.Main.BottomNav.Chats.route) {
                        Routing.Main.BottomNav.Chats.Content(
                            client,
                            users,
                            onChatClick,
                            onSearchClick,
                            navController
                        )
                    }

                    composable(Routing.Main.BottomNav.Map.route) {
                        Routing.Main.BottomNav.Map.Content(
                            geoMessages = geoMessages.toMutableList(),
                            //geoMessages = mutableListOf()
                            onLoad = {map ->
                                for (geoMessage in geoMessages) {
                                    val geoPoint = GeoPoint(geoMessage.geoMessage.latitude, geoMessage.geoMessage.longitude)
                                    val marker = Marker(map)
                                    marker.setPosition(geoPoint)
                                    marker.setIcon(BitmapDrawable(context.resources, geoMessage.user.avatar))
                                    marker.setInfoWindow(null)
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    map.overlays.add(marker)
                                }
                            },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRoutingMainContent() {
    val client = ClientManager.client

    val usersViewModel = UsersViewModel(client)
    val mapViewModel = MapViewModel(client)
    val navController = rememberNavController()

    Routing.Main.Content(
        ClientManager.client,
        usersViewModel,
        mapViewModel,
        onChatClick = { },
        onSearchClick = { },
        )

}

@Composable
fun Main(
    client: Client,
    navController: NavHostController
) {
    val usersViewModel = UsersViewModel(client)
    val mapViewModel = MapViewModel(client)
    NavHost(navController = navController, startDestination = Routing.Main.route) {
        composable(Routing.Main.route) {
            Routing.Main.Content(
                client,
                usersViewModel,
                mapViewModel,
                onChatClick = { },
                onSearchClick = { },
                navController
            )
        }
    }
}