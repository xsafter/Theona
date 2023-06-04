package org.xsafter.xmtpmessenger.activities.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.ClientSingleton
import org.xsafter.xmtpmessenger.ui.components.chat.ChatUIState
import org.xsafter.xmtpmessenger.ui.theme.JetchatTheme
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : ComponentActivity() {

    @Inject lateinit var clientSingleton: ClientSingleton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("id")

        val client: Client = clientSingleton.client!!

        setContent {
            JetchatTheme {
                val navController = rememberNavController()

                ConversationContent(
                    client,
                    userId = userId!!,
                    uiState = ChatUIState(
                        userId.take(5),
                        2,
                        mutableListOf()
                    ),
                    navigateToProfile = { user ->

                    },
                    onNavIconPressed = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}