package org.xsafter.xmtpmessenger

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import es.dmoral.toasty.Toasty

@Composable
fun WarningToast(message: String) {
    val ctx = LocalContext.current
    Toasty.warning(ctx, message, Toast.LENGTH_SHORT, true).show()
}

@Composable
fun ErrorToast(message: String) {
    val ctx = LocalContext.current
    Toasty.error(ctx, message, Toast.LENGTH_SHORT, true).show()
}
