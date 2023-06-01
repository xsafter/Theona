package org.xsafter.xmtpmessenger.activities

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.GeoMessage

class MapViewModel(client: Client) : ViewModel() {
    private val _geoMessages by lazy {
        MutableStateFlow<List<GeoMessage>>(listOf()).also { usersFlow ->
            GeoMessage(0.0, 0.0)
//            viewModelScope.launch {
//                usersFlow.value = client.conversations.list().map {
//                    it.messages().map { message ->
//                        if (message.body.startsWith("{")) {
//                            val geoMessage = Gson().fromJson(message.body, GeoMessage::class.java)
//                        }
//                    }
//                }
//            }
        }
    }

    val geoMessages: StateFlow<List<GeoMessage>> = _geoMessages
}
