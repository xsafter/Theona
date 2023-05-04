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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    lateinit var client: Client
    val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))


    private val messagesString = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            initializeClient()

            Log.d("xmtp", "account created")

            val convBuilder = ConversationHelper(client)

            val conversations =
                convBuilder.createConversation("0xaE69785837cbc9fB2cf50e6E6419a7044D80eEF3")
        val conversation = conversations[0]!!
        val geoConversation = conversations[1]!!

        geoConversation.send("0.0 0.0")
        Log.d("xmtp", "conversation created")

            // Load all messages in the conversation
            val messages = conversation.messages()

        GlobalScope.launch {
            getMessages(conversation)
        }

            Log.d("xmtp", "messages: ${messages.size}")

            Log.d("xmtp", "message sent")

            Log.d("xmtp", "messages: ${conversation.messages().size}")

            setContent {
                MessengerUI(messagesString) { message ->
                    conversation.send(text = message)
                }
            }

//        }
    }

    suspend fun getMessages(conversation: Conversation): String {
        var messages = ""
        conversation.streamMessages().collect {
            Log.e("msg", "${it.senderAddress}: ${it.body}")
             messages += "${it.senderAddress}: ${it.body}\n"
            messagesString.add("${it.senderAddress}: ${it.body}")

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

        println(keys)

        return keys
    }

    fun updateMessages(newMessage: String) {
        messagesString.add(newMessage)
    }


    fun initializeClient() {
        runBlocking {
            var keys: PrivateKeyBundleV1? = null

            val account = PrivateKeyBuilder()

            runBlocking {
                val serializedKeys = readKeys()
                if (serializedKeys != null) {
                    keys = PrivateKeyBundleV1Builder.fromEncodedData(readKeys()!!)
                }
            }

            println(keys)

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

                }
            } catch (e: NullPointerException) {
                client = Client().create(account, ClientManager.CLIENT_OPTIONS)
                // Serialize the key bundle and store it somewhere safe
                val serializedKeys =
                    PrivateKeyBundleV1Builder.encodeData(client.privateKeyBundleV1)
                runBlocking {
                    storeKeys(serializedKeys)
                }
            }

            client = Client().buildFrom(bundle = keys!!, options = options)
            PrivateKeyBundleV1Builder.encodeData(client!!.privateKeyBundleV1)

            ClientManager.createClient(keys.toString())

            Log.d("xmtp", account.address)
        }
    }
}
