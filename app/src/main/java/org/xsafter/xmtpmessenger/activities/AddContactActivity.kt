package org.xsafter.xmtpmessenger.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.activities.ui.theme.XMTPMessengerTheme
import org.xsafter.xmtpmessenger.activities.viewmodels.AddContactViewModel
import org.xsafter.xmtpmessenger.data.ClientSingleton
import javax.inject.Inject

@AndroidEntryPoint
class AddContactActivity : ComponentActivity() {
    @Inject lateinit var clientSingleton: ClientSingleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client: Client = clientSingleton.client!!
        setContent {
            XMTPMessengerTheme {
                // A surface container using the 'background' color from the theme
                AddContactScreen(viewModel = AddContactViewModel(this, client))
            }
        }
    }
}
