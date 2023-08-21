package org.xsafter.xmtpmessenger.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.xmtp.android.library.Client
import org.xmtp.android.library.ClientOptions
import org.xmtp.android.library.XMTPEnvironment

object ClientManager {

    val CLIENT_OPTIONS = ClientOptions(api = ClientOptions.Api(XMTPEnvironment.PRODUCTION))

    private val _clientState = MutableStateFlow<ClientState>(ClientState.Unknown)
    val clientState: StateFlow<ClientState> = _clientState

    private var _client: Client? = null

    val client: Client
        get() = if (clientState.value == ClientState.Ready) {
            _client!!
        } else {
            throw IllegalStateException("Client called before Ready state")
        }

    sealed class ClientState {
        object Unknown : ClientState()
        object Ready : ClientState()
        data class Error(val message: String) : ClientState()
    }
}