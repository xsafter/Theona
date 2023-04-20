package org.xsafter.xmtpmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.runBlocking
import org.xmtp.android.library.Client
import org.xmtp.android.library.ClientOptions
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.XMTPEnvironment
import org.xmtp.android.library.messages.PrivateKeyBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val account = PrivateKeyBuilder()

        Log.d("xmtp", account.address)

        // Create the client with a `SigningKey` from your app
        val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))
        val client = Client().create(account = account, options = options)
        Log.d("xmtp", "accout created")

        // Start a conversation with XMTP
        val conversation =
            client.conversations.newConversation("0xaE69785837cbc9fB2cf50e6E6419a7044D80eEF3")

        Log.d("xmtp", "conversation created")

        // Load all messages in the conversation
        val messages = conversation.messages()

        Log.d("xmtp", "messages: ${messages.size}")

        // Send a message
        conversation.send(text = "yoba")

        Log.d("xmtp", "message sent")

        Log.d("xmtp", "messages: ${conversation.messages().size}")

        runBlocking {
            getMessages(conversation)
        }
    }

    suspend fun getMessages(conversation: Conversation) {
        conversation.streamMessages().collect {
            Log.e("xmtp", "${it.senderAddress}: ${it.body}")
        }
    }
}