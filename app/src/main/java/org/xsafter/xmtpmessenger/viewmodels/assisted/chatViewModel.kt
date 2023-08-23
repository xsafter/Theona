package org.xsafter.xmtpmessenger.viewmodels.assisted

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.EntryPointAccessors
import org.xsafter.xmtpmessenger.di.ViewModelFactoryProvider
import org.xsafter.xmtpmessenger.viewmodels.ChatViewModel

@Composable
fun chatViewModel(userId: String): ChatViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        ViewModelFactoryProvider::class.java
    ).chatViewModelFactory()

    return viewModel(factory = ChatViewModel.provideFactory(factory, userId))
}