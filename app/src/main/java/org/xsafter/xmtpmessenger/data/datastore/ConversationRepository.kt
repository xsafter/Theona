package org.xsafter.xmtpmessenger.data.datastore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.DecodedMessage
import org.xmtp.android.library.messages.InvitationV1ContextBuilder
import javax.inject.Inject

class ConversationRepository @Inject constructor (val client: Client) {

    suspend fun createMainConversation(userId: String): Conversation {
        return withContext(Dispatchers.IO) {
            client.conversations.newConversation(
                userId,
                context = InvitationV1ContextBuilder.buildFromConversation("theona/text")
            )
        }
    }

    suspend fun createGeoConversation(userId: String): Conversation {
        return withContext(Dispatchers.IO) {
            client.conversations.newConversation(
                userId,
                context = InvitationV1ContextBuilder.buildFromConversation("theona/geodata")
            )
        }
    }

    suspend fun getMessages(conversation: Conversation): Flow<DecodedMessage> = flow {
        val messages = conversation.streamMessages()
        emitAll(messages)
    }
}
