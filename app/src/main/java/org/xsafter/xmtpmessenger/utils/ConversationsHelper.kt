package org.xsafter.xmtpmessenger.utils

import android.util.Log
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.xmtp.android.library.Conversation
import org.xmtp.android.library.DecodedMessage

@UiThread
fun fetchConversations() {
    MainScope().launch(Dispatchers.IO) {
        val listItems = mutableListOf<MainListItem>()
        try {
            val conversations = ClientManager.client.conversations.list()
            listItems.addAll(
                conversations.map { conversation ->
                    val lastMessage = fetchMostRecentMessage(conversation)
                    MainListItem.ConversationItem(
                        id = conversation.topic,
                        conversation,
                        lastMessage
                    )
                }
            )
        } catch (e: Exception) {
            Log.e("XMTP", "Error fetching conversations", e)
        }
    }
}

@WorkerThread
private fun fetchMostRecentMessage(conversation: Conversation): DecodedMessage? {
    return conversation.messages(limit = 1).firstOrNull()
}

sealed class MainListItem(open val id: String, val itemType: Int) {
    companion object {
        const val ITEM_TYPE_CONVERSATION = 1
        const val ITEM_TYPE_FOOTER = 2
    }

    data class ConversationItem(
        override val id: String,
        val conversation: Conversation,
        val mostRecentMessage: DecodedMessage?,
    ) : MainListItem(id, ITEM_TYPE_CONVERSATION)

    data class Footer(
        override val id: String,
        val address: String,
        val environment: String,
    ) : MainListItem(id, ITEM_TYPE_FOOTER)
}