package app.kotlin.qrquicker.ui.styles

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

const val TRANSITION_DURATION: Int = 300

val pushRightTransition: ContentTransform = slideInHorizontally(
    animationSpec = tween(
        durationMillis = TRANSITION_DURATION,
        easing = EaseOut
    ),
    initialOffsetX = { it }
).togetherWith(
    slideOutHorizontally(
        animationSpec = tween(
            durationMillis = TRANSITION_DURATION,
            easing = EaseOut
        ),
        targetOffsetX = { -it }
    )
)

val pushLeftTransition:ContentTransform = slideInHorizontally(
    animationSpec = tween(
        durationMillis = 300,
        easing = LinearEasing
    ),
    initialOffsetX = { -it }
).togetherWith(
    slideOutHorizontally(
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        targetOffsetX = { it }
    )
)