package org.xsafter.xmtpmessenger.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import org.xsafter.xmtpmessenger.data.EMOJIS.EMOJI_CLOUDS
import org.xsafter.xmtpmessenger.data.EMOJIS.EMOJI_FLAMINGO
import org.xsafter.xmtpmessenger.data.EMOJIS.EMOJI_MELTING
import org.xsafter.xmtpmessenger.data.EMOJIS.EMOJI_PINK_HEART
import org.xsafter.xmtpmessenger.data.EMOJIS.EMOJI_POINTS
import org.xsafter.xmtpmessenger.ui.components.chat.ChatUIState
import org.xsafter.xmtpmessenger.ui.components.chat.Message
import java.util.Date

data class User (
    val id: String,
    var username: String,
    var avatar: Bitmap,
    val lastMessage: String,
    var lastMessageUser: String,
    val lastMessageTime: Date
)

val me = User(
    id = "0x0",
    username = "xsafter",
    avatar = createSquareBlackBitmap(100, 100),
    lastMessage = "Hello World!",
    lastMessageUser = "me",
    lastMessageTime = Date(1685736377)
)


fun createSquareBlackBitmap(width: Int, height: Int): Bitmap {
    val config = Bitmap.Config.ARGB_8888
    val bitmap = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.BLACK)
    return bitmap
}



private val initialMessages = listOf(
    Message(
        "me",
        "Check it out!",
        1683606059000,
        authorAvatar = createSquareBlackBitmap(100, 100)
    ),
    Message(
        "me",
        "Thank you!$EMOJI_PINK_HEART",
        1664041808000,
        createSquareBlackBitmap(100, 100),
        createSquareBlackBitmap(100, 100)
    ),
    Message(
        "Taylor Brooks",
        "You can use all the same stuff",
        1672692699000,
        authorAvatar = createSquareBlackBitmap(100, 100)
    ),
    Message(
        "Taylor Brooks",
        "@aliconors Take a look at the `Flow.collectAsStateWithLifecycle()` APIs",
        1669428740000,
        authorAvatar = createSquareBlackBitmap(100, 100)
    ),
    Message(
        "John Glenn",
        "Compose newbie as well $EMOJI_FLAMINGO, have you looked at the JetNews sample? " +
                "Most blog posts end up out of date pretty fast but this sample is always up to " +
                "date and deals with async data loading (it's faked but the same idea " +
                "applies) $EMOJI_POINTS https://goo.gle/jetnews",
        1668972501000,
        authorAvatar = createSquareBlackBitmap(100, 100)
    ),
    Message(
        "me",
        "Compose newbie: I’ve scourged the internet for tutorials about async data loading but haven’t found any good ones $EMOJI_MELTING $EMOJI_CLOUDS. What’s the recommended way to load async data and emit composable widgets?",
        1664625975000,
        authorAvatar = createSquareBlackBitmap(100, 100)
    )
)

object EMOJIS {
    // EMOJI 15
    const val EMOJI_PINK_HEART = "\uD83E\uDE77"

    // EMOJI 14 🫠
    const val EMOJI_MELTING = "\uD83E\uDEE0"

    // ANDROID 13.1 😶‍🌫️
    const val EMOJI_CLOUDS = "\uD83D\uDE36\u200D\uD83C\uDF2B️"

    // ANDROID 12.0 🦩
    const val EMOJI_FLAMINGO = "\uD83E\uDDA9"

    // ANDROID 12.0  👉
    const val EMOJI_POINTS = " \uD83D\uDC49"
}

val exampleUiState = ChatUIState(
    initialMessages = initialMessages.toMutableList(),
    channelName = "#composers",
    channelMembers = 42
)