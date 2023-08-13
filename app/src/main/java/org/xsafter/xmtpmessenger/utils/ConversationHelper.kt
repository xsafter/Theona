package org.xsafter.xmtpmessenger.utils

import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.messages.InvitationV1ContextBuilder

class ConversationHelper(private val client: Client) {
    private val TEXT_LABEL = "theona/text"
    private val GEO_LABEL = "theona/geodata"

    fun createMainConversation(contact: String): Conversation {
        return client.conversations.newConversation(
            contact,
            context = InvitationV1ContextBuilder.buildFromConversation(TEXT_LABEL)
        )
    }

    fun createGeoConversation(contact: String): Conversation {
        return client.conversations.newConversation(
            contact,
            context = InvitationV1ContextBuilder.buildFromConversation(GEO_LABEL)
        )
    }
}

