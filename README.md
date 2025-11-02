# KMP Onboarding

KMP Onboarding is a Kotlin Multiplatform library that helps you build rich, animated onboarding flows with Jetpack Compose Multiplatform. It provides ready-made UI primitives for paging, navigation controls, and page indicators so you can focus on your product story instead of wiring boilerplate.

## Features
- **Compose Multiplatform first** – Works across Android, iOS, desktop, and any other platform supported by Compose Multiplatform.
- **Page modeling API** – Define onboarding pages with titles, bodies, images, and custom composable content using `PageViewModel`.
- **Built-in controls** – Skip, Next, Back, and Done buttons with callbacks that plug into your navigation logic.
- **Animated indicators** – Customizable page indicators with gradients, shapes, and transitions.
- **Flexible theming** – Override colors, typography, gradients, and global headers/footers for complete brand alignment.

## Demo

<video src="https://github.com/user-attachments/assets/8d2dc070-f0bf-4b6a-bbf3-6df429009065" controls width="640"></video>

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
