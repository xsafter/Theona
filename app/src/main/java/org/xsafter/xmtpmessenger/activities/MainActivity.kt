package org.xsafter.xmtpmessenger.activities

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.xsafter.xmtpmessenger.data.GeoMessage
import org.xsafter.xmtpmessenger.R
import org.xsafter.xmtpmessenger.activities.viewmodels.MainViewModel
import org.xsafter.xmtpmessenger.data.ClientSingleton
import org.xsafter.xmtpmessenger.data.me
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val PERMISSION_ID = 143
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mainViewModel: MainViewModel

    private var geoMessage = GeoMessage(37.4226711, -122.0849872)

    @Inject lateinit var clientSingleton: ClientSingleton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = MainViewModel(this)
        if (clientSingleton.client == null)
            mainViewModel.initializeClient()
        setupLocationServices()
        setupUI()
        val client = mainViewModel.client
        clientSingleton.client = client
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
//            val navController = androidx.navigation.compose.rememberNavController()
//            val messagesUI = MessengerUI(mainViewModel.messagesString) { message ->
//                mainViewModel.conversation.send(text = message)
//            }
//            val mapView = MapView(geoMessage = geoMessage!!, onLoad = { map: MapView ->
//                map.overlays.add(MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map))
//            })
            //BottomNav(navController = navController, ::messagesUI, ::mapView)

            Main(client = clientSingleton.client!!)
            Log.e("My adress", "${mainViewModel.client.address}, ${me.id}")
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
