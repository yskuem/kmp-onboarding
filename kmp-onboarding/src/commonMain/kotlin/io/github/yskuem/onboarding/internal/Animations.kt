package io.github.yskuem.onboarding.internal

import androidx.compose.ui.unit.Dp

internal fun lerpDp(start: Dp, end: Dp, fraction: Float): Dp {
    val f = fraction.coerceIn(0f, 1f)
    return start + (end - start) * f
}

internal fun lerpFloat(start: Float, end: Float, fraction: Float): Float {
    val f = fraction.coerceIn(0f, 1f)
    return start + (end - start) * f
}
