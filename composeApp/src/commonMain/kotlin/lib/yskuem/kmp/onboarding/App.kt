package lib.yskuem.kmp.onboarding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.yskuem.onboarding.api.DotsContainerStyle
import io.github.yskuem.onboarding.api.IntroductionScreen
import io.github.yskuem.onboarding.api.PageDecoration
import io.github.yskuem.onboarding.api.PageViewModel

@Composable
fun App() {
    MaterialTheme {
        val pages = listOf(
            PageViewModel(
                title = "AI英文法道場",
                body = "無限に新しい問題。学習ログも自動で可視化。",
                image = {
                    Icon(
                        imageVector = Icons.Outlined.EmojiObjects,
                        contentDescription = null,
                        modifier = Modifier.size(180.dp)
                    )
                },
                decoration = PageDecoration(
                    gradient = Brush.verticalGradient(
                        listOf(Color(0xFFFFF5F0), Color(0xFFFF7A59))
                    ),
                    titleTextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    bodyTextStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp)
                )
            ),
            PageViewModel(
                title = "リスニングも強化",
                body = "スクリプト同期と倍速再生で効率アップ。",
                image = {
                    Icon(
                        imageVector = Icons.Outlined.Headset,
                        contentDescription = null,
                        modifier = Modifier.size(180.dp)
                    )
                },
                decoration = PageDecoration(
                    pageColor = Color(0xFFE3F2FD)
                )
            ),
            PageViewModel(
                title = "今日から開始",
                body = "3分で1セット。毎日の習慣に最適。",
                image = {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = null,
                        modifier = Modifier.size(180.dp)
                    )
                },
                decoration = PageDecoration(
                    gradient = Brush.verticalGradient(
                        listOf(Color(0xFFE8F5E9), Color(0xFFB2DFDB))
                    )
                )
            )
        )

        IntroductionScreen(
            pages = pages,
            showSkipButton = true,
            showNextButton = true,
            showDoneButton = true,
            skip = { Text("スキップ") },
            next = { Text("次へ") },
            done = { Text("はじめる") },
            onSkip = { skipToEnd() },
            onDone = {
                // ここでナビゲーション遷移など
            },
            dotsContainerStyle = DotsContainerStyle(
                containerColor = Color.Transparent,
                contentPadding = PaddingValues(20.dp)
            )
        )
    }
}