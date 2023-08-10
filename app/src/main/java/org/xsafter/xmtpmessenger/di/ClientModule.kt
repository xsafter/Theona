package org.xsafter.xmtpmessenger.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.xmtp.android.library.Client
import org.xmtp.android.library.ClientOptions
import org.xmtp.android.library.XMTPEnvironment
import org.xmtp.android.library.messages.PrivateKeyBuilder
import org.xmtp.android.library.messages.PrivateKeyBundleV1
import org.xmtp.android.library.messages.PrivateKeyBundleV1Builder
import org.xsafter.xmtpmessenger.utils.ClientManager
import org.xsafter.xmtpmessenger.utils.DataStoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClientModule {

    private val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))
    @Provides
    @Singleton
    fun provideClient(dataStoreManager: DataStoreManager): Client {
        var client: Client ? = null

        runBlocking {
            val serializedKeys = dataStoreManager.readKeys()
            var keys: PrivateKeyBundleV1? = null
            if (serializedKeys != null) {
                keys = PrivateKeyBundleV1Builder.fromEncodedData(serializedKeys)
            }
            val account = PrivateKeyBuilder()

            if (keys == null) {
                try {
                    client = Client().create(account, ClientManager.CLIENT_OPTIONS)

                    val serializedKeys = PrivateKeyBundleV1Builder.encodeData(client!!.privateKeyBundleV1)

                    dataStoreManager.storeKeys(serializedKeys)

                } catch (e: NullPointerException) {
                    // Handle or log specific exceptions
                }
            } else {
                client = Client().buildFrom(bundle = keys!!, options = options)
            }
        }
        return client!!
    }
}
