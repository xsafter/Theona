package org.xsafter.xmtpmessenger.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.xsafter.xmtpmessenger.Blue10
import org.xsafter.xmtpmessenger.Blue20
import org.xsafter.xmtpmessenger.Blue30
import org.xsafter.xmtpmessenger.Blue40
import org.xsafter.xmtpmessenger.Blue80
import org.xsafter.xmtpmessenger.Blue90
import org.xsafter.xmtpmessenger.BlueGrey30
import org.xsafter.xmtpmessenger.BlueGrey50
import org.xsafter.xmtpmessenger.BlueGrey60
import org.xsafter.xmtpmessenger.BlueGrey80
import org.xsafter.xmtpmessenger.BlueGrey90
import org.xsafter.xmtpmessenger.DarkBlue10
import org.xsafter.xmtpmessenger.DarkBlue20
import org.xsafter.xmtpmessenger.DarkBlue30
import org.xsafter.xmtpmessenger.DarkBlue40
import org.xsafter.xmtpmessenger.DarkBlue80
import org.xsafter.xmtpmessenger.DarkBlue90
import org.xsafter.xmtpmessenger.Grey10
import org.xsafter.xmtpmessenger.Grey20
import org.xsafter.xmtpmessenger.Grey80
import org.xsafter.xmtpmessenger.Grey90
import org.xsafter.xmtpmessenger.Grey95
import org.xsafter.xmtpmessenger.Grey99
import org.xsafter.xmtpmessenger.Red10
import org.xsafter.xmtpmessenger.Red20
import org.xsafter.xmtpmessenger.Red30
import org.xsafter.xmtpmessenger.Red40
import org.xsafter.xmtpmessenger.Red80
import org.xsafter.xmtpmessenger.Red90
import org.xsafter.xmtpmessenger.Yellow10
import org.xsafter.xmtpmessenger.Yellow20
import org.xsafter.xmtpmessenger.Yellow30
import org.xsafter.xmtpmessenger.Yellow40
import org.xsafter.xmtpmessenger.Yellow80
import org.xsafter.xmtpmessenger.Yellow90

private val JetchatDarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue40,
    secondary = DarkBlue80,
    onSecondary = DarkBlue20,
    secondaryContainer = DarkBlue30,
    onSecondaryContainer = DarkBlue90,
    tertiary = Yellow80,
    onTertiary = Yellow20,
    tertiaryContainer = Yellow30,
    onTertiaryContainer = Yellow90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey20,
    surfaceVariant = BlueGrey30,
    onSurfaceVariant = BlueGrey80,
    outline = BlueGrey60
)

private val JetchatLightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    inversePrimary = Blue80,
    secondary = DarkBlue40,
    onSecondary = Color.White,
    secondaryContainer = DarkBlue90,
    onSecondaryContainer = DarkBlue10,
    tertiary = Yellow40,
    onTertiary = Color.White,
    tertiaryContainer = Yellow90,
    onTertiaryContainer = Yellow10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = BlueGrey90,
    onSurfaceVariant = BlueGrey30,
    outline = BlueGrey50
)

@SuppressLint("NewApi")
@Composable
fun JetchatTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val myColorScheme = when {
        dynamicColor && isDarkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        dynamicColor && !isDarkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        isDarkTheme -> JetchatDarkColorScheme
        else -> JetchatLightColorScheme
    }

    MaterialTheme(
        colorScheme = myColorScheme,
        //typography = JetchatTypography,
        content = content
    )
}