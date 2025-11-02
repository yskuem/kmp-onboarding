# KMP Onboarding

KMP Onboarding is a Kotlin Multiplatform library that helps you build rich, animated onboarding flows with Jetpack Compose Multiplatform. It provides ready-made UI primitives for paging, navigation controls, and page indicators so you can focus on your product story instead of wiring boilerplate.

## Features
- **Compose Multiplatform first** – Works across Android, iOS, desktop, and any other platform supported by Compose Multiplatform.
- **Page modeling API** – Define onboarding pages with titles, bodies, images, and custom composable content using `PageViewModel`.
- **Built-in controls** – Skip, Next, Back, and Done buttons with callbacks that plug into your navigation logic.
- **Animated indicators** – Customizable page indicators with gradients, shapes, and transitions.
- **Flexible theming** – Override colors, typography, gradients, and global headers/footers for complete brand alignment.

## Getting started
Add the dependency to the `commonMain` source set of your Compose Multiplatform project:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.yskuem:kmp-onboarding:1.0.2")
            }
        }
    }
}
```

## Usage example
Below is a simple onboarding flow that demonstrates how to configure pages, customize controls, and react to user actions.

```kotlin
@Composable
fun OnboardingScreen() {
    val pages = listOf(
        PageViewModel(
            title = "Welcome",
            body = "Discover curated lessons tailored to your goals.",
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
        ),
        PageViewModel(
            title = "Track progress",
            body = "Set reminders and stay motivated with daily streaks.",
            decoration = PageDecoration(pageColor = Color(0xFFF3E5F5))
        )
    )

    IntroductionScreen(
        pages = pages,
        showSkipButton = true,
        showNextButton = true,
        showBackButton = true,
        showDoneButton = true,
        skip = { Text("Skip") },
        next = { Text("Next") },
        back = { Text("Back") },
        done = { Text("Get started") },
        onSkip = { skipToEnd() },
        onDone = {
            // Navigate to your authenticated area here
        },
        dotsContainerStyle = DotsContainerStyle(
            containerColor = Color.Transparent,
            contentPadding = PaddingValues(20.dp)
        )
    )
}
```

### Advanced customization
- Pass `rawPages` if you need full control over each page's composable content.
- Supply your own `IntroState` via `rememberIntroState` to control paging programmatically.
- Customize `DotsDecorator` to adjust indicator size, shape, colors, and spacing.
- Use `globalHeader` and `globalFooter` slots to inject persistent content like logos or call-to-action buttons.

## Sample application
A working sample is included under [`composeApp`](./composeApp/src/commonMain/kotlin/lib/yskuem/kmp/onboarding/App.kt). You can run it on Android or iOS to see the onboarding experience in action.

## License
Please refer to the repository for licensing information and usage terms.
