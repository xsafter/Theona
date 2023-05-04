package org.xsafter.xmtpmessenger

import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.messages.InvitationV1ContextBuilder

class ConversationHelper(val client: Client) {
    private var mainConversation: Conversation? = null
    private var geoConversation: Conversation? = null

    private val TEXT_LABEL = "theona/text"
    private val GEO_LABEL = "theona/geodata"

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
        // Get all the conversations
        val conversations = client.conversations.list()

        // Filter for the ones from your app
        val textConversations = conversations.filter {
            val conversationId = it.conversationId ?: return@filter false
            conversationId.startsWith(TEXT_LABEL)
        }

        // Filter for the ones from your app
        val geoConversations = conversations.filter {
            val conversationId = it.conversationId ?: return@filter false
            conversationId.startsWith(GEO_LABEL)
        }

        return arrayOf(textConversations, geoConversations)
    }


}