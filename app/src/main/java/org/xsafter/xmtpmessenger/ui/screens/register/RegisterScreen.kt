package org.xsafter.xmtpmessenger.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import org.xsafter.xmtpmessenger.R
import org.xsafter.xmtpmessenger.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    navController: NavHostController
) {
    val usernameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordState = remember { mutableStateOf(TextFieldValue()) }


    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()


    val isDarkTheme = isSystemInDarkTheme()
    val l_background = if (isDarkTheme) {
        R.drawable.l_abstract_dark
    } else {
        R.drawable.l_abstract_light
    }
    val r_background = if (isDarkTheme) {
        R.drawable.r_abstract_dark
    } else {
        R.drawable.r_abstract_light
    }
    val logo = if (isDarkTheme) {
        painterResource(id = R.drawable.logo_dark)
    } else {
        painterResource(id = R.drawable.th_logo_1)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            //.padding(horizontal = 16.dp)
            .paint(
                alignment = Alignment.TopStart,
                painter =
                rememberAsyncImagePainter(l_background, imageLoader)
            )
            .paint(
                alignment = Alignment.BottomEnd,
                painter =
                rememberAsyncImagePainter(r_background, imageLoader)
            )
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = logo,
                contentDescription = "Logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopCenter)
            )
        }


        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.paddingFromBaseline(top = 40.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPasswordState.value,
            onValueChange = { confirmPasswordState.value = it },
            label = { Text("Confirm password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.weight(1f))



        Button(
            onClick = {
                      if (usernameState.value.text.isNotEmpty() && passwordState.value.text.isNotEmpty() && confirmPasswordState.value.text.isNotEmpty()
                          && passwordState.value.text == confirmPasswordState.value.text
                          && passwordState.value.text.length  >= 6) {


                          viewModel.saveCredentials(usernameState.value.text, passwordState.value.text)
                          navController.navigate("main")
                      }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Create account")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigate to login screen
//        TextButton(
//            onClick = { /* Navigate to login screen */ },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Already have an account? Log in")
//        }
    }
}

@Preview
@Composable
fun RegisterPreview() {
    RegisterScreen(viewModel = hiltViewModel(), navController = rememberNavController())
}