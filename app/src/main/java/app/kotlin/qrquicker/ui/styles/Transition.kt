package app.kotlin.qrquicker.ui.styles

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import app.kotlin.qrquicker.TRANSITION_DURATION

val slideInFromRight: EnterTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(
        durationMillis = TRANSITION_DURATION,
        easing = LinearEasing
    )
)

val slideInFromLeft: EnterTransition = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(
        durationMillis = TRANSITION_DURATION,
        easing = LinearEasing
    )
)

val slideOutFromRight: ExitTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(
        durationMillis = TRANSITION_DURATION,
        easing = LinearEasing
    )
)

val slideOutFromLeft: ExitTransition = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(
        durationMillis = TRANSITION_DURATION,
        easing = LinearEasing
    )
)