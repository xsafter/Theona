package org.xsafter.xmtpmessenger.activities

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.xmtp.android.library.Client
import org.xmtp.android.library.ClientOptions
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.XMTPEnvironment
import org.xmtp.android.library.messages.PrivateKeyBuilder
import org.xmtp.android.library.messages.PrivateKeyBundleV1
import org.xmtp.android.library.messages.PrivateKeyBundleV1Builder
import org.xsafter.xmtpmessenger.ClientManager
import org.xsafter.xmtpmessenger.ConversationHelper
import org.xsafter.xmtpmessenger.GeoMessage
import org.xsafter.xmtpmessenger.MapView
import org.xsafter.xmtpmessenger.R
import org.xsafter.xmtpmessenger.ui.BottomNav

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "keys")

class MainActivity : AppCompatActivity() {

    private val USER_KEY = stringPreferencesKey("user_key")
    private val PERMISSION_ID = 143
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var client: Client
    private val options = ClientOptions(api = ClientOptions.Api(env = XMTPEnvironment.PRODUCTION, isSecure = true))
    private val messagesString = mutableStateListOf<String>()
    private lateinit var conversation: Conversation
    private lateinit var geoConversation: Conversation

    private var geoMessage = GeoMessage(37.4226711, -122.0849872)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeClient()
        setupLocationServices()
        setupConversations()
        loadMessages()
        setupUI()
    }

    private fun setupLocationServices() {
        if (!checkPermissions()) {
            requestPermissions()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
    }

    private fun setupConversations() {
        val convBuilder = ConversationHelper(client)
        val conversations = convBuilder.createConversation("0xaE69785837cbc9fB2cf50e6E6419a7044D80eEF3")

        conversation = conversations[0]!!
        //println(conversation.messages(limit = 5))
        geoConversation = conversations[1]!!
    }

    private fun loadMessages() {
        MainScope().launch {
            getMessages(conversation)
        }
    }

    private fun setupUI() {
        setContent {
            val navController = androidx.navigation.compose.rememberNavController()
//            MessengerUI(messagesString) { message ->
//                conversation.send(text = message)
//            }
            MapView(geoMessage = geoMessage!!, onLoad = { map: MapView ->
                map.overlays.add(MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map))
            })
            BottomNav(navController = navController)
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

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            geoConversation.send(Gson().toJson(GeoMessage(location.latitude, location.longitude)))
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {
            //WarningToast(message = "Location disabled")
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //TODO
            }
        }
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

            Log.d("xmtp", account.address)
        }
    }
}
