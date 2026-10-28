package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RyotaColorScheme = lightColorScheme(
  primary = OrangeSolidButton,
  onPrimary = DisplayTextWhite,
  primaryContainer = PeachPastelButton,
  onPrimaryContainer = KeypadNumberText,
  secondary = PeachPastelButton,
  onSecondary = DisplayTextWhite,
  background = KeypadBackground,
  onBackground = KeypadNumberText,
  surface = KeypadBackground,
  onSurface = KeypadNumberText,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = OrangeGradientTop.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = RyotaColorScheme,
    typography = Typography,
    content = content,
  )
}
