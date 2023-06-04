package org.xsafter.xmtpmessenger.activities.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.activities.MainActivity
import org.xsafter.xmtpmessenger.data.ClientSingleton
import org.xsafter.xmtpmessenger.data.exampleUiState
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
                ConversationContent(
                    client,
                    userId = userId!!,
                    uiState = exampleUiState,
                    navigateToProfile = { user ->

                    },
                    onNavIconPressed = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}