package org.xsafter.xmtpmessenger.utils

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

}
