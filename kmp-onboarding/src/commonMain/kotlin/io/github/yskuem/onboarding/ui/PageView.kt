package io.github.yskuem.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.yskuem.onboarding.api.PageViewModel
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PageView(
    index: Int,
    pagerState: PagerState,
    page: PageViewModel,
) {
    val pageOffset by remember {
        derivedStateOf {
            (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
        }
    }

    val dec = page.decoration
    val bgColor = dec.pageColor
    val gradient = dec.gradient
    val hasBgLayer = gradient != null || (bgColor != Color.Unspecified)

    val titleAlpha = (1f - abs(pageOffset)).coerceIn(0f, 1f)
    val bodyAlpha = (1f - abs(pageOffset) * 1.2f).coerceIn(0f, 1f)
    val imageParallaxX = (-40f * pageOffset)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                when {
                    gradient != null -> Modifier.background(gradient)
                    hasBgLayer -> Modifier.background(bgColor)
                    else -> Modifier
                }
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(8.dp))

            Crossfade(
                targetState = page.titleWidget != null || !page.title.isNullOrBlank(),
                label = "title"
            ) { hasTitle ->
                if (hasTitle) {
                    Box(
                        Modifier
                            .padding(top = 12.dp)
                            .graphicsLayer {
                                alpha = titleAlpha
                                translationY = 8f * abs(pageOffset)
                            }
                    ) {
                        if (page.titleWidget != null) {
                            page.titleWidget.invoke()
                        } else {
                            Text(text = page.title.orEmpty(), style = dec.titleTextStyle)
                        }
                    }
                } else {
                    Spacer(Modifier.height(8.dp))
                }
            }

            AnimatedVisibility(
                visible = page.image != null,
                enter = fadeIn(tween(250)),
                exit = fadeOut(tween(200)),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = dec.maxImageHeight)
                    .weight(1f),
                label = "image"
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier.graphicsLayer {
                            translationX = imageParallaxX
                            alpha = (1f - abs(pageOffset) * 0.5f).coerceIn(0.2f, 1f)
                            scaleX = 1f - abs(pageOffset) * 0.05f
                            scaleY = 1f - abs(pageOffset) * 0.05f
                        }
                    ) {
                        page.image?.invoke()
                    }
                }
            }

            Crossfade(
                targetState = page.bodyWidget != null || !page.body.isNullOrBlank(),
                label = "body"
            ) { hasBody ->
                if (hasBody) {
                    Box(
                        Modifier.graphicsLayer {
                            alpha = bodyAlpha
                            translationY = 10f * abs(pageOffset)
                        }
                    ) {
                        if (page.bodyWidget != null) {
                            page.bodyWidget.invoke()
                        } else {
                            Text(text = page.body.orEmpty(), style = dec.bodyTextStyle)
                        }
                    }
                } else {
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (page.footer != null) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    page.footer.invoke()
                }
            }
        }
    }
}
