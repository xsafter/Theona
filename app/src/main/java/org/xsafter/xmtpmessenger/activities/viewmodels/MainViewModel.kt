package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.MainScope
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
import org.xsafter.xmtpmessenger.ClientManager
import org.xsafter.xmtpmessenger.ConversationHelper
import org.xsafter.xmtpmessenger.data.me
import org.xsafter.xmtpmessenger.ui.components.createFromObject
import javax.inject.Inject



class MainViewModel @Inject constructor(
    @ApplicationContext context: Context
)
    : ViewModel() {

    val context = context
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keys")

    private val USER_KEY = stringPreferencesKey("user_key")

    lateinit var client: Client
    private val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))
    public val messagesString = mutableStateListOf<String>()
    public lateinit var conversation: Conversation
    public lateinit var geoConversation: Conversation


    fun setupConversations() {
        val convBuilder = ConversationHelper(client)
        val conversations = convBuilder.createConversation("0xaE69785837cbc9fB2cf50e6E6419a7044D80eEF3")

        conversation = conversations[0]!!
        //println(conversation.messages(limit = 5))
        geoConversation = conversations[1]!!
    }

    fun loadMessages() {
        MainScope().launch {
            getMessages(conversation)
        }
    }

    suspend fun getMessages(conversation: Conversation) {

        messagesString.add(
            geoConversation.messages(limit = 25).joinToString("\n") {
                "${it.senderAddress}: ${it.body}"
            }
        )

        geoConversation.streamMessages().collect {             //messages += "${it.senderAddress}: ${it.body}\n"
            messagesString.add("${it.senderAddress}: ${it.body}")
        }

    }

    suspend fun storeKeys(serializedKeys: String) {
        context.dataStore.edit { settings ->
            settings[USER_KEY] = serializedKeys
        }

    }

    suspend fun readKeys(): String? {
        val dataStore = context.dataStore
        val keys = dataStore.data.map { preferences ->
            preferences[USER_KEY]
        }.first()

        return keys
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


            try {
                if (keys == null) {
                    client = Client().create(account, ClientManager.CLIENT_OPTIONS)
                    // Serialize the key bundle and store it somewhere safe
                    val serializedKeys =
                        PrivateKeyBundleV1Builder.encodeData(client.privateKeyBundleV1)

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

            me.username = client.address
            me.avatar = createFromObject(client.address)

            Log.d("xmtp", account.address)
        }
    }

    fun send(message: String) {
        conversation.send(message)
    }
}