package org.xsafter.xmtpmessenger.utils

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import es.dmoral.toasty.Toasty

@Composable
fun WarningToast(message: String) {
    val ctx = LocalContext.current
    Toasty.warning(ctx, message, Toast.LENGTH_SHORT, true).show()
}

@Composable
fun ErrorToast(message: String) {
    val ctx = LocalContext.current
    Toasty.error(ctx, message, Toast.LENGTH_SHORT, true).show()
}


@Composable
fun getCurrentLocation(context: Context = LocalContext.current, onLocationReceived: (Location) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Request location updates
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            // Handle the received location here
            location?.let {
                onLocationReceived(it)
            }
        }
        .addOnFailureListener { exception: Exception ->
            // Handle the exception here
        }
}