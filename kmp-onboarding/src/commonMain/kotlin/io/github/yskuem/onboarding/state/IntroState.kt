package io.github.yskuem.onboarding.state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

@OptIn(ExperimentalFoundationApi::class)
class IntroState internal constructor(
    internal val pagerState: PagerState,
    internal var pageCountProvider: () -> Int,
) {

    suspend fun next() {
        val last = pageCountProvider() - 1
        val next = (pagerState.currentPage + 1).coerceAtMost(last)
        pagerState.animateScrollToPage(
            page = next,
            animationSpec = spring(stiffness = 500f)
        )
    }

    suspend fun previous() {
        val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
        pagerState.animateScrollToPage(
            page = prev,
            animationSpec = spring(stiffness = 500f)
        )
    }

    suspend fun skipToEnd() {
        pagerState.animateScrollToPage(
            page = pageCountProvider() - 1,
            animationSpec = tween(350)
        )
    }

    suspend fun animateScrollTo(index: Int) {
        val clamped = index.coerceIn(0, pageCountProvider() - 1)
        pagerState.animateScrollToPage(
            page = clamped,
            animationSpec = tween(350)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberIntroState(
    pageCountProvider: () -> Int
): IntroState {
    // PagerState も同様に pageCount をラムダで受け取るAPI
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = pageCountProvider
    )
    return remember {
        IntroState(
            pagerState = pagerState,
            pageCountProvider = pageCountProvider
        )
    }
}
