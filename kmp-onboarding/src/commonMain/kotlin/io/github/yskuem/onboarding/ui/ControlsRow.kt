package io.github.yskuem.onboarding.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import io.github.yskuem.onboarding.api.DotsContainerStyle
import io.github.yskuem.onboarding.api.DotsDecorator
import io.github.yskuem.onboarding.api.IntroButtonStyle
import io.github.yskuem.onboarding.state.IntroState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ControlsRow(
    pagerState: PagerState,
    pageCount: Int,
    showSkipButton: Boolean,
    showNextButton: Boolean,
    showBackButton: Boolean,
    showDoneButton: Boolean,
    skip: (@Composable () -> Unit)?,
    next: (@Composable () -> Unit)?,
    back: (@Composable () -> Unit)?,
    done: (@Composable () -> Unit)?,
    onSkip: (suspend IntroState.() -> Unit)?,
    onDone: (suspend IntroState.() -> Unit)?,
    decorator: DotsDecorator,
    containerStyle: DotsContainerStyle,
    nextButtonStyle: IntroButtonStyle,
    doneButtonStyle: IntroButtonStyle,
    state: IntroState,
) {
    val isLast = pagerState.currentPage == pageCount - 1
    val scope = rememberCoroutineScope()

    Surface(
        color = containerStyle.containerColor,
        shape = containerStyle.shape,
        shadowElevation = containerStyle.shadowElevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(containerStyle.contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                !isLast && showSkipButton && skip != null -> {
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (onSkip != null) state.onSkip() else state.skipToEnd()
                            }
                        },
                        modifier = Modifier.semantics { role = Role.Button }
                    ) { skip() }
                }
                showBackButton && pagerState.currentPage > 0 && back != null -> {
                    TextButton(
                        onClick = { scope.launch { state.previous() } },
                        modifier = Modifier.semantics { role = Role.Button }
                    ) { back() }
                }
                else -> Spacer(Modifier.width(64.dp))
            }

            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                WormDots(
                    count = pageCount,
                    current = pagerState.currentPage,
                    progress = pagerState.currentPageOffsetFraction,
                    decorator = decorator,
                )
            }

            when {
                isLast && showDoneButton && done != null -> {
                    Button(
                        onClick = {
                            scope.launch {
                                if (onDone != null) state.onDone()
                            }
                        },
                        contentPadding = doneButtonStyle.contentPadding ?: PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                        shape = doneButtonStyle.shape ?: RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (doneButtonStyle.containerColor != Color.Unspecified) 
                                doneButtonStyle.containerColor else ButtonDefaults.buttonColors().containerColor,
                            contentColor = if (doneButtonStyle.contentColor != Color.Unspecified) 
                                doneButtonStyle.contentColor else ButtonDefaults.buttonColors().contentColor
                        )
                    ) { done() }
                }
                !isLast && showNextButton && next != null -> {
                    FilledTonalButton(
                        onClick = { scope.launch { state.next() } },
                        contentPadding = nextButtonStyle.contentPadding ?: PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                        shape = nextButtonStyle.shape ?: RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (nextButtonStyle.containerColor != Color.Unspecified) 
                                nextButtonStyle.containerColor else ButtonDefaults.filledTonalButtonColors().containerColor,
                            contentColor = if (nextButtonStyle.contentColor != Color.Unspecified) 
                                nextButtonStyle.contentColor else ButtonDefaults.filledTonalButtonColors().contentColor
                        )
                    ) { next() }
                }
                else -> Spacer(Modifier.width(64.dp))
            }
        }
    }
}
