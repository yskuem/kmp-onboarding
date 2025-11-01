package io.github.yskuem.onboarding.api

import androidx.compose.ui.graphics.Brush
import io.github.yskuem.onboarding.state.IntroState
import io.github.yskuem.onboarding.state.rememberIntroState
import io.github.yskuem.onboarding.ui.ControlsRow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.yskuem.onboarding.ui.PageView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroductionScreen(
    pages: List<PageViewModel> = emptyList(),
    rawPages: List<@Composable () -> Unit>? = null,
    modifier: Modifier = Modifier,

    // Buttons visibility
    showSkipButton: Boolean = true,
    showNextButton: Boolean = true,
    showBackButton: Boolean = false,
    showDoneButton: Boolean = true,

    // Buttons content
    skip: (@Composable () -> Unit)? = { Text("skip") },
    next: (@Composable () -> Unit)? = { Text("next") },
    back: (@Composable () -> Unit)? = { Text("back") },
    done: (@Composable () -> Unit)? = { Text("start", fontWeight = FontWeight.SemiBold) },

    onSkip: (suspend IntroState.() -> Unit)? = null,
    onDone: (suspend IntroState.() -> Unit)? = null,
    onChange: ((Int) -> Unit)? = null,

    dotsDecorator: DotsDecorator = DotsDecorator(),
    dotsContainerStyle: DotsContainerStyle = DotsContainerStyle(),

    globalHeader: (@Composable () -> Unit)? = null,
    globalFooter: (@Composable () -> Unit)? = null,
    globalBackgroundColor: Color = Color.Unspecified,

    emptyContent: (@Composable () -> Unit)? = { CircularProgressIndicator() },

    state: IntroState = rememberIntroState {
        (rawPages?.size ?: pages.size).coerceAtLeast(1)
    },
) {
    val pageCount = rawPages?.size ?: pages.size

    if (pageCount <= 0) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .semantics { stateDescription = "Page 0 / 0" },
            color = if (globalBackgroundColor != Color.Unspecified)
                globalBackgroundColor else MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(0.dp),
            shadowElevation = 0.dp,
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                if (globalHeader != null) Box(Modifier.fillMaxWidth()) { globalHeader() }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    emptyContent?.invoke()
                }
                if (globalFooter != null) Box(Modifier.fillMaxWidth()) { globalFooter() }
            }
        }
        return
    }

    val pagerState = state.pagerState

    val settled by remember { derivedStateOf { pagerState.settledPage } }
    LaunchedEffect(settled) { onChange?.invoke(settled) }


    val currentIndex by remember { derivedStateOf { pagerState.currentPage } }
    val bgSpec = remember(currentIndex, pages, rawPages) {
        if (rawPages != null) BGSkin(color = globalBackgroundColor, brush = null)
        else {
            val dec = pages.getOrNull(currentIndex)?.decoration
            val brush = dec?.gradient
            val color = when {
                brush != null -> Color.Transparent
                dec?.pageColor != null && dec.pageColor != Color.Unspecified -> dec.pageColor
                globalBackgroundColor != Color.Unspecified -> globalBackgroundColor
                else -> Color.Transparent
            }
            BGSkin(color = color, brush = brush)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                stateDescription = "Page ${pagerState.currentPage + 1} / $pageCount"
            },
        color = if (bgSpec.brush == null) bgSpec.color else Color.Transparent,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (bgSpec.brush != null) Modifier.background(bgSpec.brush) else Modifier
                )
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                if (globalHeader != null) Box(Modifier.fillMaxWidth()) { globalHeader() }

                // pages with parallax
                Box(Modifier.weight(1f)) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { index ->
                        if (rawPages != null) {
                            rawPages[index]()
                        } else {
                            val p = pages[index]
                            PageView(
                                index = index,
                                pagerState = pagerState,
                                page = p
                            )
                        }
                    }
                }

                ControlsRow(
                    pagerState = pagerState,
                    pageCount = pageCount,
                    showSkipButton = showSkipButton,
                    showNextButton = showNextButton,
                    showBackButton = showBackButton,
                    showDoneButton = showDoneButton,
                    skip = skip,
                    next = next,
                    back = back,
                    done = done,
                    onSkip = onSkip,
                    onDone = onDone,
                    decorator = dotsDecorator,
                    containerStyle = dotsContainerStyle,
                    state = state,
                )

                if (globalFooter != null) Box(Modifier.fillMaxWidth()) { globalFooter() }
            }
        }
    }
}

private data class BGSkin(val color: Color, val brush: Brush?)
