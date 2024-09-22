package app.kotlin.qrquicker.ui.styles

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import app.kotlin.qrquicker.R

private val robotoFamily: FontFamily = FontFamily(
    Font(resId = R.font.roboto_regular, weight = FontWeight.Normal),
    Font(resId = R.font.roboto_medium, weight = FontWeight.Medium)
)

val fontFamilyPrimary: FontFamily = robotoFamily

val fontWeightRegular: FontWeight = FontWeight.Normal
val fontWeightMedium: FontWeight = FontWeight.Medium

val fontSizeRoot: TextUnit = 14.sp
val fontSizeScaleUp1: TextUnit = 16.sp
val fontSizeScaleUp2: TextUnit = 22.sp
val fontSizeScaleDown1: TextUnit = 12.sp