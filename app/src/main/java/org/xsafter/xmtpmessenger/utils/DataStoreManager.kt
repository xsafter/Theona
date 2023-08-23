package org.xsafter.xmtpmessenger.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val USER_KEY = stringPreferencesKey("user_key")

    suspend fun storeKeys(serializedKeys: String) {
        dataStore.edit { settings ->
            settings[USER_KEY] = serializedKeys
        }
    }

    suspend fun readKeys(): String? {
        val keys = dataStore.data.map { preferences ->
            preferences[USER_KEY]
        }.first()

        return keys
    }
}
