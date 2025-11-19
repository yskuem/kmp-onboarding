package io.github.yskuem.onboarding.api

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues


data class PageDecoration(
    val pageColor: Color = Color.Unspecified,
    val gradient: Brush? = null,
    val titleTextStyle: TextStyle = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
    ),
    val bodyTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp
    ),
    val maxImageHeight: Dp = 320.dp,
)



data class PageViewModel(
    val title: String? = null,
    val body: String? = null,
    val image: (@androidx.compose.runtime.Composable () -> Unit)? = null,
    val titleWidget: (@androidx.compose.runtime.Composable () -> Unit)? = null,
    val bodyWidget: (@androidx.compose.runtime.Composable () -> Unit)? = null,
    val footer: (@androidx.compose.runtime.Composable () -> Unit)? = null,
    val decoration: PageDecoration = PageDecoration(),
)



data class DotsDecorator(
    val size: Dp = 8.dp,
    val activeSize: DpSize = DpSize(22.dp, 8.dp),
    val color: Color = Color(0x33000000),
    val activeColor: Color = Color(0xFF6666FF),
    val spacing: Dp = 8.dp,
    val shape: Shape = CircleShape,
    val activeShape: Shape = RoundedCornerShape(12.dp),
)



data class DotsContainerStyle(
    val containerColor: Color = Color.Transparent,
    val contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp
    ),
    val shape: Shape = RoundedCornerShape(0.dp),
    val shadowElevation: Dp = 0.dp,
)

data class IntroButtonStyle(
    val containerColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
)
