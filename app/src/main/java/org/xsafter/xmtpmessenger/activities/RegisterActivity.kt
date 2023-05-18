package org.xsafter.xmtpmessenger.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.credentialsDataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen(viewModel = RegisterViewModel(credentialsDataStore))
        }
    }
}