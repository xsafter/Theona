package org.xsafter.xmtpmessenger.ui.screens.add

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.dmoral.toasty.Toasty
import io.sentry.compose.SentryTraced
import org.xmtp.android.library.XMTPException
import org.xsafter.xmtpmessenger.viewmodels.AddContactViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddContactScreen(viewModel: AddContactViewModel, navController: NavHostController) {
    SentryTraced("add_contact") {
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
                    .padding(top = 32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            var myAdress by remember { mutableStateOf(viewModel.myAdress)}

            Text(
                text = "Insert the user ID to add a contact",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = username,
                onValueChange = { newUsername -> viewModel.onUsernameChanged(newUsername) },
                label = { Text("Username") },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "My adress:",
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )



            val ctx = LocalContext.current
            // Add the OK button
            Button(
                onClick = {
                    try {
                        if (!viewModel.checkAvailability())
                            Toasty.error(ctx, "Can't add user: not found", Toast.LENGTH_SHORT, true)
                                .show()
                        else
                            viewModel.addUser()
                    } catch (e: XMTPException) {
                        Toasty.error(ctx, "Something went wrong", Toast.LENGTH_SHORT, true).show()
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
}

@Composable
fun AddressView(address: String) {
    // This is for the ripple animation
    val interactionSource = remember { MutableInteractionSource() }
    var isAddressCopied by remember { mutableStateOf(false) }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    // Add a ripple when the element is pressed
    val pressIndicator = rememberRipple(bounded = false)

    Column {
        Row(modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "My adress:", style = MaterialTheme.typography.titleMedium)
        }
        Row(modifier = Modifier.padding(16.dp, 0.dp)) {
            Button(
                modifier = Modifier
                    .padding(0.dp, 8.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    )
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                onClick = {
                    clipboardManager.setText(AnnotatedString(address))
                }
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = "copy adress",  modifier = Modifier.padding(end = 8.dp))
                Text(text = address, style = MaterialTheme.typography.bodyLarge, fontFamily =
                FontFamily.Monospace)
            }
        }
    }
}

@Composable
@Preview
fun AddressViewPreview() {
    AddressView("0x2bJ7o2YLd8y8HqoF7tTkKY7yz4UZeXyRn")
}