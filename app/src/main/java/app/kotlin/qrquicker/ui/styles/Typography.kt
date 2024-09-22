package app.kotlin.qrquicker.ui.styles

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle

@Composable
fun TextStyle.noScale(): TextStyle {
    val currentFontScale: Float = LocalConfiguration.current.fontScale
    return this.copy(fontSize = this.fontSize / currentFontScale)
}

val title: TextStyle = TextStyle(
    fontFamily = fontFamilyPrimary,
    fontWeight = fontWeightRegular,
    fontSize = fontSizeScaleUp2
)

val bodyLarge: TextStyle = TextStyle(
    fontFamily = fontFamilyPrimary,
    fontWeight = fontWeightRegular,
    fontSize = fontSizeScaleUp1
)

val bodySmall: TextStyle = TextStyle(
    fontFamily = fontFamilyPrimary,
    fontWeight = fontWeightRegular,
    fontSize = fontSizeRoot
)

val labelLarge: TextStyle = TextStyle(
    fontFamily = fontFamilyPrimary,
    fontWeight = fontWeightMedium,
    fontSize = fontSizeRoot
)

val labelSmall: TextStyle = TextStyle(
    fontFamily = fontFamilyPrimary,
    fontWeight = fontWeightMedium,
    fontSize = fontSizeScaleDown1
)