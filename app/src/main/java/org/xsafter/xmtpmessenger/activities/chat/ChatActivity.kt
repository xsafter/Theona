package org.xsafter.xmtpmessenger.activities.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.xsafter.xmtpmessenger.activities.MainActivity
import org.xsafter.xmtpmessenger.data.exampleUiState
import org.xsafter.xmtpmessenger.ui.theme.JetchatTheme

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("id")

        setContent {
            JetchatTheme {
                ConversationContent(
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