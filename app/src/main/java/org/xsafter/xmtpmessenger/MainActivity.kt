package org.xsafter.xmtpmessenger

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.xmtp.android.library.Client
import org.xmtp.android.library.ClientOptions
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.XMTPEnvironment
import org.xmtp.android.library.messages.PrivateKeyBuilder
import org.xmtp.android.library.messages.PrivateKeyBundleV1
import org.xmtp.android.library.messages.PrivateKeyBundleV1Builder

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keys")

class MainActivity : AppCompatActivity() {

    private val USER_KEY = stringPreferencesKey("user_key")

    // Create the client with a `SigningKey` from your app
    val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        GlobalScope.launch {

            var keys:  PrivateKeyBundleV1?  = null

            val account = PrivateKeyBuilder()

            var client: Client

            try {
                if (keys == null) {
                    client = Client().create(account, ClientManager.CLIENT_OPTIONS)
                    // Serialize the key bundle and store it somewhere safe
                    val serializedKeys =
                        PrivateKeyBundleV1Builder.encodeData(client.privateKeyBundleV1)

                    println(serializedKeys)
                    runBlocking {
                        storeKeys(serializedKeys)
                    }

                    //return
                }
            } catch (e: NullPointerException) {
                client = Client().create(account, ClientManager.CLIENT_OPTIONS)
                // Serialize the key bundle and store it somewhere safe
                val serializedKeys =
                    PrivateKeyBundleV1Builder.encodeData(client.privateKeyBundleV1)
                runBlocking {
                    storeKeys(serializedKeys)
                }

                return
            }

            runBlocking {
                //keys = readKeys()?.let { PrivateKeyBundleV1Builder.fromEncodedData(it) }

                keys = PrivateKeyBundleV1Builder.fromEncodedData("CsABCIfB0Mf8MBIiCiAfuH7fgVT4t63nzqz58VGgPOYbVYlZRfPgbeJW3LewlRqSAQiJwdDH/DASRApCCkAJNk3AyJUk9qO+V4jeZHHom89rlu6kghpAeC789imKcyGbiimz0C3Ua35W2Mwctw9vYM/Kz3SQ8R6bZ7dQrTXVGkMKQQR2Fn2fZhAPNzHcE4uDxmwScCUFZAWpIKY0gBlRO9fTHQHRw93c9YBhXneNWOeoTOnOuHY4Jur7neBl92uj9K5wEsIBCJvB0Mf8MBIiCiACee4haaxOOWAtpp9vt/F7D8kbExUcwfh4nycGCp3oJhqUAQibwdDH/DASRgpECkCwxOi+YNX0eElKkhTrWfzLJqsdkFtbBYYs2ELC00V/9gus4X2ag9M4foEonVibF26uQykeliBpPhVJ0mWgfqCGEAEaQwpBBOmNzSuo3BPKF4BOkXNYstAvLpkJ4UlSpVTrbUW85SmXAZe4QS/cdwubwKb+0Z+PzAQT70u4GvYaWSbSzll+AOA=")
            }

            client = Client().buildFrom(bundle = keys!!, options = options)
            PrivateKeyBundleV1Builder.encodeData(client!!.privateKeyBundleV1)

            ClientManager.createClient(keys.toString())

            Log.d("xmtp", account.address)


            // Create the client with a `SigningKey` from your app
             //client = ClientManager.client
            Log.d("xmtp", "account created")

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

            var messagesString: String

            runBlocking {
                messagesString = getMessages(conversation)
            }

            setContent {
                SimpleTextField()
                textLogger(messagesString)
            }
//        }
    }

    suspend fun getMessages(conversation: Conversation): String {
        var messages = ""
        conversation.streamMessages().collect {
            Log.e("xmtp", "${it.senderAddress}: ${it.body}")
             messages += "${it.senderAddress}: ${it.body}\n"
        }

        return messages
    }

    @Composable
    fun textLogger(messagesList: String) {
        Text(text = messagesList)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SimpleTextField(): String {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            }
        )

        return text.text
    }

    suspend fun storeKeys(serializedKeys: String) {
        dataStore.edit { settings ->
            settings[USER_KEY] = serializedKeys
        }

    }

    suspend fun readKeys(): String? {
        val dataStore = applicationContext.dataStore
        val keys = dataStore.data.map { preferences ->
            preferences[USER_KEY]
        }.first()

        return keys
    }

}
