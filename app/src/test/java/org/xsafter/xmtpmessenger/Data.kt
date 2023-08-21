package org.xsafter.xmtpmessenger

import org.xsafter.xmtpmessenger.data.model.User
import org.xsafter.xmtpmessenger.data.model.createSquareBlackBitmap
import java.util.Date

val me = User(
    id = "0x0",
    username = "xsafter",
    avatar = createSquareBlackBitmap(100, 100),
    lastMessage = "Hello World!",
    lastMessageUser = "me",
    lastMessageTime = Date(1685736377)
)