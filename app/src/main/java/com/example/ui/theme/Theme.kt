package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = VibrantPurple,
    secondary = CardHighlightBg,
    tertiary = VibrantPurpleDark,
    background = Color(0xFF141218),
    surface = Color(0xFF1D1B20),
    onPrimary = Color.White,
    onSecondary = ActivePurpleText,
    onBackground = LightVioletBg,
    onSurface = LightVioletBg
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantPurple,
    secondary = CardHighlightBg,
    tertiary = VibrantPurpleDark,
    background = LightVioletBg,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = ActivePurpleText,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+ (setting false by default to ensure brand colors shine)
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
