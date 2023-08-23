package org.xsafter.xmtpmessenger.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val isLocationTurnedOn = dataStore.data.map {
        it[locationOnKey] ?: false
    }

    suspend fun setLocationTurnedOn(isStarted: Boolean) = withContext(Dispatchers.IO) {
        dataStore.edit {
            it[locationOnKey] = isStarted
        }
    }

    private companion object {
        val locationOnKey = booleanPreferencesKey("is_location_on")
    }
}