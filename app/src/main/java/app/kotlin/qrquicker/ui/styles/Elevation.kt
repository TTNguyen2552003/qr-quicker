package app.kotlin.qrquicker.ui.styles

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private fun Modifier.dropShadow(
    shape: Shape = RectangleShape,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    spreadRadius: Dp = 0.dp,
    color: Color = Color(color = 0xFF000000)
) = this.drawBehind {
    drawIntoCanvas { canvas: Canvas ->
        val shadowSize = Size(
            width = size.width + spreadRadius.toPx(),
            height = size.height + spreadRadius.toPx()
        )
        val shadowOutline = shape.createOutline(
            size = shadowSize,
            layoutDirection = layoutDirection,
            density = this
        )
        val paint = Paint()
        paint.color = color
        if (blurRadius.toPx() > 0) {
            paint.asFrameworkPaint().apply {
                maskFilter = BlurMaskFilter(
                    blurRadius.toPx(),
                    BlurMaskFilter.Blur.NORMAL
                )
            }
        }
        canvas.save()
        canvas.translate(
            dx = offsetX.toPx(),
            dy = offsetY.toPx()
        )
        canvas.drawOutline(
            outline = shadowOutline,
            paint = paint
        )
        canvas.restore()
    }
}

private data class ElevationValues(
    val layer1OffsetX: Dp,
    val layer1OffsetY: Dp,
    val layer1BlurRadius: Dp,
    val layer1SpreadRadius: Dp,
    val layer2OffsetX: Dp,
    val layer2OffsetY: Dp,
    val layer2BlurRadius: Dp,
    val layer2SpreadRadius: Dp
)

private val elevationValues = mapOf(
    Elevation.EXTRA_LOW to ElevationValues(0.dp, 1.dp, 2.dp, 0.dp, 0.dp, 2.dp, 4.dp, (-1).dp),
    Elevation.LOW to ElevationValues(0.dp, 2.dp, 4.dp, (-1).dp, 0.dp, 4.dp, 8.dp, (-2).dp)
)

enum class Elevation {
    EXTRA_LOW,
    LOW
}

fun Modifier.elevation(shape: Shape, elevation: Elevation): Modifier {
    val values: ElevationValues =
        elevationValues[elevation] ?: throw IllegalArgumentException("Invalid elevation")
    return this
        .dropShadow(
            shape = shape,
            offsetX = values.layer1OffsetX,
            offsetY = values.layer1OffsetY,
            blurRadius = values.layer1BlurRadius,
            spreadRadius = values.layer1SpreadRadius,
            color = shadowLayerColor1
        )
        .dropShadow(
            shape = shape,
            offsetX = values.layer2OffsetX,
            offsetY = values.layer2OffsetY,
            blurRadius = values.layer2BlurRadius,
            spreadRadius = values.layer2SpreadRadius,
            color = shadowLayerColor2
        )
}