package org.xsafter.xmtpmessenger.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.xsafter.xmtpmessenger.hasPermission

class LocationPermissionState(
    private val activity: ComponentActivity,
    private val onResult: (LocationPermissionState) -> Unit
) {

    /** Whether permission was granted to access approximate location. */
    var accessCoarseLocationGranted by mutableStateOf(false)
        private set

    /** Whether to show a rationale for permission to access approximate location. */
    var accessCoarseLocationNeedsRationale by mutableStateOf(false)
        private set

    /** Whether permission was granted to access precise location. */
    var accessFineLocationGranted by mutableStateOf(false)
        private set

    /** Whether to show a rationale for permission to access precise location. */
    var accessFineLocationNeedsRationale by mutableStateOf(false)
        private set

    /**
     * Whether to show a degraded experience (set after the permission is denied).
     */
    var showDegradedExperience by mutableStateOf(false)
        private set

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            updateState()
            showDegradedExperience = !hasPermission()
            onResult(this)
        }

    init {
        updateState()
    }

    private fun updateState() {
        accessCoarseLocationGranted = activity.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        accessCoarseLocationNeedsRationale =
            activity.shouldShowRationaleFor(Manifest.permission.ACCESS_COARSE_LOCATION)
        accessFineLocationGranted = activity.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        accessFineLocationNeedsRationale =
            activity.shouldShowRationaleFor(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Launch the permission request. Note that this may or may not show the permission UI if the
     * permission has already been granted or if the user has denied permission multiple times.
     */
    fun requestPermissions() {
        permissionLauncher.launch(locationPermissions)
    }

    fun hasPermission(): Boolean = accessCoarseLocationGranted || accessFineLocationGranted

    fun shouldShowRationale(): Boolean = !hasPermission() && (
            accessCoarseLocationNeedsRationale || accessFineLocationNeedsRationale)
}

private val locationPermissions =
    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

internal fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

internal fun Activity.shouldShowRationaleFor(permission: String): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)