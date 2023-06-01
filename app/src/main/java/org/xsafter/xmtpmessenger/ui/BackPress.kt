package org.xsafter.xmtpmessenger.ui

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun backPressHandler(onBackPressed: () -> Unit, enabled: Boolean = true) {
    val dispatcher = AmbientBackPressedDispatcher.current.onBackPressedDispatcher

    val backCallback = remember(onBackPressed) {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    LaunchedEffect(enabled) {
        backCallback.isEnabled = enabled
    }

    DisposableEffect(dispatcher, onBackPressed) {
        dispatcher.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}

val AmbientBackPressedDispatcher =
    staticCompositionLocalOf<OnBackPressedDispatcherOwner> { error("Ambient used without Provider") }