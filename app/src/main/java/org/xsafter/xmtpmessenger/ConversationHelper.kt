package org.xsafter.xmtpmessenger

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.DecodedMessage
import org.xmtp.android.library.messages.InvitationV1ContextBuilder

class ConversationHelper(val client: Client) {
    private var mainConversation: Conversation? = null
    private var geoConversation: Conversation? = null

    private val TEXT_LABEL = "theona/text"
    private val GEO_LABEL = "theona/geodata"

    sealed class MessageListItem(open val id: String, val itemType: Int) {
        companion object {
            const val ITEM_TYPE_MESSAGE = 1
        }

        data class Message(override val id: String, val message: DecodedMessage) :
            MessageListItem(id, ITEM_TYPE_MESSAGE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val streamMessages: StateFlow<MessageListItem?> =
        stateFlow(GlobalScope, null) { subscriptionCount ->
            if (mainConversation == null) {
                mainConversation = ClientManager.client.fetchConversation(TEXT_LABEL)
            }
            if (mainConversation != null) {
                mainConversation!!.streamMessages()
                    .flowWhileShared(
                        subscriptionCount,
                        SharingStarted.WhileSubscribed(1000L)
                    )
                    .flowOn(Dispatchers.IO)
                    .distinctUntilChanged()
                    .mapLatest { message ->
                        MessageListItem.Message(message.id, message)
                    }
                    .catch { emptyFlow<MessageListItem>() }
            } else {
                emptyFlow()
            }
        }

    fun createConversation(contact: String): Array<Conversation?> {
        mainConversation = client.conversations.newConversation(
            contact,
            context = InvitationV1ContextBuilder.buildFromConversation(TEXT_LABEL)
        )
        geoConversation = client.conversations.newConversation(
            contact,
            context = InvitationV1ContextBuilder.buildFromConversation(GEO_LABEL)
        )

        return arrayOf(mainConversation, geoConversation)
    }

    fun getAllConversations(contact: String): Array<List<Conversation>> {
        // Get all the conversations with all users
        val conversations = client.conversations.list()

        // Filter for the regular conversation
        val textConversations = conversations.filter {
            val conversationId = it.conversationId ?: return@filter false
            conversationId.startsWith(TEXT_LABEL)
        }

        // Filter for the conversation with geodata
        val geoConversations = conversations.filter {
            val conversationId = it.conversationId ?: return@filter false
            conversationId.startsWith(GEO_LABEL)
        }

        return arrayOf(textConversations, geoConversations)
    }


}
