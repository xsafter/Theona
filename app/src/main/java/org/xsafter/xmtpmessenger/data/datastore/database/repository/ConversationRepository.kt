package org.xsafter.xmtpmessenger.data.datastore.database.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
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
            val conversation = client.conversations.newConversation(
                userId,
                context = InvitationV1ContextBuilder.buildFromConversation("theona/geodata")
            )
            Log.d("ConversationRepository", "Conversation created: $conversation")
            conversation
        }
    }

    suspend fun getMessages(conversation: Conversation): Flow<DecodedMessage> = flow {
        val messages = conversation.messages().asFlow()
        //Log.d("MESSAGES", messages.toString())
        val streamMessages = conversation.streamMessages()
        val messageFlow = merge(messages, streamMessages)
        emitAll(messageFlow)
    }

    suspend fun sendMessage(conversation: Conversation, message: String) {
        withContext(Dispatchers.IO) {
            conversation.send(message)
        }
    }
}
