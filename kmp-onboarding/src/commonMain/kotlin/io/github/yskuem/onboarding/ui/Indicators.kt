package io.github.yskuem.onboarding.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.github.yskuem.onboarding.api.DotsDecorator
import io.github.yskuem.onboarding.internal.lerpDp
import io.github.yskuem.onboarding.internal.lerpFloat

@Composable
internal fun WormDots(
    count: Int,
    current: Int,
    progress: Float,
    decorator: DotsDecorator,
) {
    val dir = if (progress >= 0f) 1 else -1
    val target = (current + dir).coerceIn(0, count - 1)
    val frac = kotlin.math.abs(progress).coerceIn(0f, 1f)

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { index ->
            val isCurrent = index == current
            val isTarget = index == target

            val baseW = decorator.size
            val activeW = decorator.activeSize.width
            val baseH = decorator.size
            val activeH = decorator.activeSize.height

            val targetW = when {
                isCurrent -> lerpDp(activeW, baseW, frac)
                isTarget -> lerpDp(baseW, activeW, frac)
                else -> baseW
            }
            val targetH = when {
                isCurrent -> lerpDp(activeH, baseH, frac)
                isTarget -> lerpDp(baseH, activeH, frac)
                else -> baseH
            }

            val aw by animateDpAsState(targetValue = targetW, label = "dotW")
            val ah by animateDpAsState(targetValue = targetH, label = "dotH")

            val baseAlpha = 0.55f
            val activeAlphaCurrent = lerpFloat(1f, baseAlpha, frac)
            val activeAlphaTarget = lerpFloat(baseAlpha, 1f, frac)
            val alphaTarget = when {
                isCurrent -> activeAlphaCurrent
                isTarget -> activeAlphaTarget
                else -> baseAlpha
            }
            val alpha by animateFloatAsState(targetValue = alphaTarget, label = "dotA")

            val shape = when {
                isCurrent -> decorator.activeShape
                isTarget -> decorator.activeShape
                else -> decorator.shape
            }

            val color = when {
                isCurrent -> decorator.activeColor
                isTarget -> decorator.activeColor
                else -> decorator.color
            }.copy(alpha = alpha)

            Box(
                Modifier
                    .size(aw, ah)
                    .clip(shape)
                    .background(color)
            )

            if (index != count - 1) Spacer(Modifier.width(decorator.spacing))
        }
    }
}
