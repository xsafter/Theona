package org.xsafter.xmtpmessenger.viewmodels

import android.content.ServiceConnection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.xsafter.xmtpmessenger.ForegroundLocationServiceConnection
import org.xsafter.xmtpmessenger.data.LocationRepository
import org.xsafter.xmtpmessenger.data.datastore.LocationPreferences
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    locationRepository: LocationRepository,
    private val serviceConnection: ForegroundLocationServiceConnection,
    private val locationPreferences: LocationPreferences
): ViewModel(), ServiceConnection by serviceConnection {
    val isReceivingLocationUpdates = locationRepository.isReceivingLocationUpdates

    fun toggleLocationUpdates() {
        if (isReceivingLocationUpdates.value) {
            stopLocationUpdates()
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        serviceConnection.service?.startLocationUpdates()
        // Store that the user turned on location updates.
        // It's possible that the service was not connected for the above call. In that case, when
        // the service eventually starts, it will check the persisted value and react appropriately.
        viewModelScope.launch {
            locationPreferences.setLocationTurnedOn(true)
        }
    }

    private fun stopLocationUpdates() {
        serviceConnection.service?.stopLocationUpdates()
        // Store that the user turned off location updates.
        // It's possible that the service was not connected for the above call. In that case, when
        // the service eventually starts, it will check the persisted value and react appropriately.
        viewModelScope.launch {
            locationPreferences.setLocationTurnedOn(false)
        }
    }
}