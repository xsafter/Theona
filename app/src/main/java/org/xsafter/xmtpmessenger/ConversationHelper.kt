package org.xsafter.xmtpmessenger

import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.messages.InvitationV1ContextBuilder

class ConversationHelper(val client: Client) {
    private var mainConversation: Conversation? = null
    private var geoConversation: Conversation? = null

    fun createConversation(contact: String): Map<Conversation?, Conversation?> {
        mainConversation = client.conversations.newConversation(
            contact,
            context = InvitationV1ContextBuilder.buildFromConversation("theona/text")
        )
        geoConversation = client.conversations.newConversation(
            contact,
            context = InvitationV1ContextBuilder.buildFromConversation("theona/geodata")
        )

        return mapOf(mainConversation to geoConversation)
    }

    fun getConversations(contact: String): Map<List<Conversation>, List<Conversation>> {
        // Get all the conversations
        val conversations = client.conversations.list()

        // Filter for the ones from your app
        val textConversations = conversations.filter {
            val conversationId = it.conversationId ?: return@filter false
            conversationId.startsWith("theona/text")
        }

        // Filter for the ones from your app
        val geoConversations = conversations.filter {
            val conversationId = it.conversationId ?: return@filter false
            conversationId.startsWith("theona/text")
        }

        return mapOf(textConversations to geoConversations)
    }


}