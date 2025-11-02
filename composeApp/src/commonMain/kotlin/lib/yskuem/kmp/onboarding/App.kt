package lib.yskuem.kmp.onboarding

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.yskuem.onboarding.api.DotsContainerStyle
import io.github.yskuem.onboarding.api.IntroductionScreen
import io.github.yskuem.onboarding.api.PageDecoration
import io.github.yskuem.onboarding.api.PageViewModel

// The following types come from your kmp-onboarding library.
// Let your IDE auto-import them to avoid typos in package names.
@Composable
fun App(onFinished: () -> Unit = {}) {
    MaterialTheme {
        OnboardingScreen(onFinished = onFinished)
    }
}

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    // Define pages with title/body and optional image/decoration.
    val pages = listOf(
        PageViewModel(
            title = "Welcome",
            body = "Turn your notes into bite-size quizzes and master grammar faster.",
            image = {
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                )
            },
            decoration = PageDecoration(
                gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF5F0),
                        Color(0xFFFFE2D6)
                    )
                )
            )
        ),
        PageViewModel(
            title = "Auto-generated Quizzes",
            body = "Create quizzes from photos of your textbooks and notes.",
            image = {
                Icon(
                    imageVector = Icons.Outlined.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                )
            },
            decoration = PageDecoration(pageColor = Color(0xFFEDEBFA)) // subtle lilac tint
        ),
        PageViewModel(
            title = "Daily Progress",
            body = "Track streaks and stay motivated with lightweight reminders.",
            image = {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                )
            },
            decoration = PageDecoration(pageColor = Color(0xFFE4F5F1))
        )
    )

    // If you want programmatic control, provide your own state:
    // val state = rememberIntroState { pages.size }
    IntroductionScreen(
        pages = pages,
        // state = state, // uncomment if you use rememberIntroState above
        showSkipButton = true,
        showNextButton = true,
        showBackButton = true,
        showDoneButton = true,

        // Button slots
        skip = { Text("Skip") },
        next = { Text("Next") },
        back = { Text("Back") },
        done = { Text("Get Started") },

        // Callbacks
        onSkip = { /* Jump to the end or mark onboarding as seen */ },
        onDone = { onFinished() },

        // Indicator container styling
        dotsContainerStyle = DotsContainerStyle(
            containerColor = Color.Transparent,
            contentPadding = PaddingValues(20.dp)
        )

        // You can also provide globalHeader / globalFooter for a persistent logo/CTA.
    )
}
