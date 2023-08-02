package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.xmtp.android.library.Client
import org.xmtp.android.library.ClientOptions
import org.xmtp.android.library.XMTPEnvironment
import org.xmtp.android.library.messages.PrivateKeyBuilder
import org.xmtp.android.library.messages.PrivateKeyBundleV1
import org.xmtp.android.library.messages.PrivateKeyBundleV1Builder
import org.xsafter.xmtpmessenger.ClientManager
import javax.inject.Inject


/*class MainViewModel @Inject constructor(
    @ApplicationContext context: Context
)
    : ViewModel() {

    val context = context
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keys")

    private val USER_KEY = stringPreferencesKey("user_key")

    lateinit var client: Client
    private val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))


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

            val serializedKeys = readKeys()
            if (serializedKeys != null) {
                keys = PrivateKeyBundleV1Builder.fromEncodedData(serializedKeys)
            }


            try {
                if (keys == null) {
                    client = Client().create(account, ClientManager.CLIENT_OPTIONS)
                    // Serialize the key bundle and store it somewhere safe
                    val serializedKeys =
                        PrivateKeyBundleV1Builder.encodeData(client.privateKeyBundleV1)

                    viewModelScope.launch {
                        storeKeys(serializedKeys)
                    }

                }
            } catch (e: NullPointerException) {
                client = Client().create(account, ClientManager.CLIENT_OPTIONS)
                // Serialize the key bundle and store it somewhere safe
                val serializedKeys =
                    PrivateKeyBundleV1Builder.encodeData(client.privateKeyBundleV1)
                viewModelScope.launch {
                    storeKeys(serializedKeys)
                }
            }

            client = Client().buildFrom(bundle = keys!!, options = options)
            PrivateKeyBundleV1Builder.encodeData(client!!.privateKeyBundleV1)

            ClientManager.createClient(keys.toString())

            Log.d("xmtp", "Client created")

            me.username = client.address
            me.avatar = createFromObject(client.address)

            Log.d("xmtp", account.address)
        }
    }
}*/

class MainViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    val context = context
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keys")

    private val USER_KEY = stringPreferencesKey("user_key")

    lateinit var client: Client
    private val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))

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
            withContext(Dispatchers.IO) {
                val serializedKeys = readKeys()
                var keys: PrivateKeyBundleV1? = null
                if (serializedKeys != null) {
                    keys = PrivateKeyBundleV1Builder.fromEncodedData(serializedKeys)
                }
                val account = PrivateKeyBuilder()

                if (keys == null) {
                    try {
                        client = Client().create(account, ClientManager.CLIENT_OPTIONS)

                        val serializedKeys = PrivateKeyBundleV1Builder.encodeData(client.privateKeyBundleV1)

                        storeKeys(serializedKeys)

                    } catch (e: NullPointerException) {
                        // Handle or log specific exceptions
                    }
                } else {
                    client = Client().buildFrom(bundle = keys!!, options = options)
                }
            }
        }
    }
}
