package org.xsafter.xmtpmessenger.ui.screens.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.sentry.compose.SentryTraced
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.model.User
import org.xsafter.xmtpmessenger.ui.components.Content
import org.xsafter.xmtpmessenger.ui.components.chat.ChatUIState
import org.xsafter.xmtpmessenger.ui.navigation.BottomBar
import org.xsafter.xmtpmessenger.ui.navigation.Routing
import org.xsafter.xmtpmessenger.ui.screens.chat.ConversationContent
import org.xsafter.xmtpmessenger.utils.ClientManager
import org.xsafter.xmtpmessenger.utils.getCurrentLocation
import org.xsafter.xmtpmessenger.viewmodels.MapViewModel
import org.xsafter.xmtpmessenger.viewmodels.UsersViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Routing.Main.Content(
    client: Client,
    usersViewModel: UsersViewModel,
    mapViewModel: MapViewModel,
    onChatClick: (user: User) -> Unit,
    onSearchClick: () -> Unit,
    navController: NavHostController = rememberNavController(),
    mainNavController: NavHostController
) {
    SentryTraced("main_screen_content") {
        val bottomNavController = rememberNavController()
        val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
        fun currentRouting(): Routing.Main.BottomNav? {
            val currentRoute = navBackStackEntry?.destination?.route
            return bottomNavRoutings.find { it.route == currentRoute }
        }

        val users: LazyPagingItems<User> = usersViewModel.users.collectAsLazyPagingItems()

        Log.e("users_list", users.itemSnapshotList.items.joinToString())


        val usersList by usersViewModel.usersList.observeAsState(initial = listOf())
        val usernames = usersList.map { it.username }.toMutableList()
        mapViewModel.setupGeoConversations(usernames)

        val geoMessages = mapViewModel.geoMessages
        Log.e("geo_messages", geoMessages.value.joinToString())

        var lat = 0.0
        var lng = 0.0
        getCurrentLocation {
            lat = it.latitude
            lng = it.longitude
        }

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
                                    if (selectedRouting is Routing.Main.BottomNav.Chats)
                                        bottomNavController.popBackStack(
                                            bottomNavController.graph.startDestinationId,
                                            false
                                        )
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
                                navController,
                                mainNavController
                            )
                        }

                        composable(Routing.Main.BottomNav.Map.route) {
                            Routing.Main.BottomNav.Map.Content(lat = lat, lng = lng, zoom = 12.0, geoMessageFlow = geoMessages)
                        }
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
    val context = LocalContext.current

    val usersViewModel: UsersViewModel = hiltViewModel()
    val mapViewModel: MapViewModel = hiltViewModel()
    val navController = rememberNavController()
    val mainNavController = rememberNavController()

    Routing.Main.Content(
        ClientManager.client,
        usersViewModel,
        mapViewModel,
        onChatClick = { },
        onSearchClick = { },
        navController = navController,
        mainNavController = mainNavController
        )

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Main(
    client: Client,
    navController: NavHostController,
    mainNavController: NavHostController
) {
    SentryTraced("main_screen") {

        val usersViewModel: UsersViewModel = hiltViewModel()
        val mapViewModel: MapViewModel = hiltViewModel()


        NavHost(navController = navController, startDestination = Routing.Main.route) {
            composable(Routing.Main.route) {
                Routing.Main.Content(
                    client,
                    usersViewModel,
                    mapViewModel,
                    onChatClick = { },
                    onSearchClick = { },
                    navController,
                    mainNavController
                )
            }
            composable(
                "chat/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) {
                val userId = it.arguments?.getString("userId")
                
                ConversationContent(
                    userId = userId!!,
                    uiState = ChatUIState(
                        "${userId.take(14)}...",
                        2,
                        mutableListOf()
                    ),
                    navigateToProfile = { user ->

                    },
                    navController = navController,
                    onNavIconPressed = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}


