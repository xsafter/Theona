package org.xsafter.xmtpmessenger.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

data class User (
    val id: String,
    val username: String,
    val avatar: Bitmap
)

val me = User(
    id = "0x0",
    username = "xsafter",
    avatar = createSquareBlackBitmap(100, 100)
)


fun createSquareBlackBitmap(width: Int, height: Int): Bitmap {
    val config = Bitmap.Config.ARGB_8888
    val bitmap = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.BLACK)
    return bitmap
}