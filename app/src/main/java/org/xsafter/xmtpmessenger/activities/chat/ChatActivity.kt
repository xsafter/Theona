package org.xsafter.xmtpmessenger.activities.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.ClientSingleton
import org.xsafter.xmtpmessenger.ui.theme.JetchatTheme
import javax.inject.Inject

class ChatActivity : ComponentActivity() {

    @Inject lateinit var clientSingleton: ClientSingleton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("id")

        val client: Client = clientSingleton.client!!

        setContent {
            JetchatTheme {
                val navController = rememberNavController()


            }
        }
    }
}