package org.xsafter.xmtpmessenger.viewmodels

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

val usernameKey = stringPreferencesKey("username")
val hashedPasswordKey = stringPreferencesKey("hashed_password")


private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = "SomeRandomSecret"

class RegisterViewModel (
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    // Save username and hashed password to DataStore
    fun saveCredentials(username: String, password: String) {
        viewModelScope.launch {
            val hashedPassword = hashPassword(password)

            dataStore.edit { preferences ->
                preferences[usernameKey] = username
                preferences[hashedPasswordKey] = hashedPassword
            }
        }
    }

    suspend fun readKeys(): String? {
        val dataStore = dataStore
        val keys = dataStore.data.map { preferences ->
            preferences[usernameKey]
        }.first()

        Log.d("KEYS", keys.toString())

        return keys
    }

    private fun hashPassword(password: String): String {
        val salt = generateRandomSalt()
        val hash = generateHash(password, salt.toHexString())
        return "$salt$hash"
    }

    private fun generateRandomSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        return salt
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }


    private fun generateHash(password: String, salt: String): String {
        val combinedSalt = "$salt$SECRET".toByteArray()

        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
        val key: SecretKey = factory.generateSecret(spec)
        val hash: ByteArray = key.encoded

        return hash.toHexString()
    }

    fun isLoggedIn(): Boolean {
        var isLoggedIn = false
        viewModelScope.launch {
            isLoggedIn = dataStore.data.first().contains(usernameKey)
        }
        return isLoggedIn
    }

    // check if at least one user is registered
    suspend fun isRegistered(): Boolean {
        return (readKeys() != null && readKeys() != "")
    }


}
