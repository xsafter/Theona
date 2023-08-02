package org.xsafter.xmtpmessenger.activities

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.dmoral.toasty.Toasty
import org.xmtp.android.library.XMTPException
import org.xsafter.xmtpmessenger.activities.viewmodels.AddContactViewModel

@Composable
fun AddContactScreen(viewModel: AddContactViewModel, navController: NavHostController) {
    val username by viewModel.username.observeAsState("")

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = "Add Contact Icon",
            modifier = Modifier
                .size(48.dp)
                .padding(top = 32.dp)
        )

        Text(
            text = "Insert the user ID to add a contact",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { newUsername -> viewModel.onUsernameChanged(newUsername) },
            label = { Text("Username") },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )

        val ctx = LocalContext.current
        // Add the OK button
        Button(
            onClick = {
                try {
                    viewModel.addUser()
                } catch (e: XMTPException) {
                    Toasty.error(ctx, "Can't add user: not found", Toast.LENGTH_SHORT, true).show()
                }

                navController.popBackStack()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("OK")
        }

        // Add other UI elements and functions to handle adding the contact in your app
    }
}