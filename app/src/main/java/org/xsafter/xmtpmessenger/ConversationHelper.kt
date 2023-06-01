package org.xsafter.xmtpmessenger

import android.util.Log
import androidx.annotation.UiThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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


    @UiThread
    fun fetchMessages(conversation: Conversation?, conversationTopic: String) {
        var conversation = conversation
        MainScope().launch(Dispatchers.IO) {
            val listItems = mutableListOf<MessageListItem>()
            try {
                if (conversation == null) {
                    conversation = ClientManager.client.fetchConversation(conversationTopic)
                }
                conversation?.let {
                    listItems.addAll(
                        it.messages().map { message ->
                            MessageListItem.Message(message.id, message)
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ConversationHelper", "Error fetching messages", e)
            }
        }
    }

    fun createConversation(contact: String): Array<Conversation?> {
        mainConversation = client.conversations.newConversation(
            contact,
            context = InvitationV1ContextBuilder.buildFromConversation(TEXT_LABEL)
        )

        println(mainConversation!!.messages())

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
