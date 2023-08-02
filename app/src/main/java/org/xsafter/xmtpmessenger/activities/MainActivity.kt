package org.xsafter.xmtpmessenger.activities

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.GeoMessage
import org.xsafter.xmtpmessenger.R
import org.xsafter.xmtpmessenger.activities.viewmodels.AddContactViewModel
import org.xsafter.xmtpmessenger.activities.viewmodels.MainViewModel
import org.xsafter.xmtpmessenger.activities.viewmodels.RegisterViewModel
import org.xsafter.xmtpmessenger.activities.viewmodels.SplashViewModel
import org.xsafter.xmtpmessenger.data.ClientSingleton
import org.xsafter.xmtpmessenger.data.me
import org.xsafter.xmtpmessenger.ui.theme.JetchatTheme
import javax.inject.Inject

val Context.credentialsDataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    private lateinit var mainViewModel: MainViewModel

    private val PERMISSION_ID = 143
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var geoMessage = GeoMessage(37.4226711, -122.0849872)

    @Inject lateinit var clientSingleton: ClientSingleton
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splashScreen.setKeepOnScreenCondition{viewModel.isLoading.value}

        mainViewModel = MainViewModel(this)
        //if (clientSingleton.client == null)
            mainViewModel.initializeClient()
        setupLocationServices()
        val client = mainViewModel.client
        clientSingleton.client = client
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

    private fun setupUI() {
        setContent {
            JetchatTheme {
                val navController = rememberNavController()
                val navControllerMainScreen = rememberNavController()

                NavigationComponent(navController = navController,
                    navControllerMainScreen = navControllerMainScreen,
                    client = mainViewModel.client)

                Log.e("My address", "${mainViewModel.client.address}, ${me.id}")
            }

        }
    }


    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
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
}

@Composable
fun NavigationComponent(navController: NavHostController, navControllerMainScreen: NavHostController,client: Client) {
    val registerViewModel = RegisterViewModel(LocalContext.current.credentialsDataStore)
    var startDestination by remember { mutableStateOf("register") }

    LaunchedEffect(key1 = registerViewModel) {
        startDestination = if(registerViewModel.isRegistered()) "main" else "register"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("register") {
            RegisterScreen(
                viewModel = registerViewModel,
                navController = navController
            )
        }

        composable("main") {
            Main(
                client = client,
                LocalContext.current,
                navController = navControllerMainScreen,
                navController
            )
        }

        composable("add_contact") {
            AddContactScreen(viewModel =
                AddContactViewModel(
                    context = LocalContext.current,
                    client = client
                ),
                navController = navController
            )
        }
    }
}