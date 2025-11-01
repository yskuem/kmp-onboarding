import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import java.util.Base64

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
}

val signingKeyFromEnv = providers.environmentVariable("GPG_KEY_CONTENTS").orNull
val signingKeyBase64 = providers.environmentVariable("GPG_KEY_CONTENTS_B64").orNull
val signingKeyPassword = providers.environmentVariable("SIGNING_PASSWORD").orNull
val signingKeyId = providers.environmentVariable("SIGNING_KEY_ID").orNull

val resolvedSigningKey = signingKeyFromEnv ?: signingKeyBase64?.let { encoded ->
    runCatching { String(Base64.getDecoder().decode(encoded)) }.getOrNull()
}

resolvedSigningKey?.let { extra["signingInMemoryKey"] = it }
signingKeyPassword?.let { extra["signingInMemoryKeyPassword"] = it }
signingKeyId?.let { extra["signingInMemoryKeyId"] = it }

providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull?.let {
    extra["mavenCentralUsername"] = it
}
providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull?.let {
    extra["mavenCentralPassword"] = it
}

kotlin {
    androidLibrary {
        namespace = "io.github.yskuem.onboarding"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "kmp-onboardingKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Android-specific dependencies
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                // iOS-specific dependencies
            }
        }
    }
}

mavenPublishing {
    coordinates("io.github.yskuem", "kmp-onboarding", "1.0.0")

    publishToMavenCentral()
    if (resolvedSigningKey != null && signingKeyPassword != null) {
        signAllPublications()
    } else {
        logger.warn("Signing key not configured. Publications will not be signed.")
    }

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release")
        )
    )

    pom {
        name = "KMP Onboarding"
        description = "Onboarding UI library for Kotlin Multiplatform"
        inceptionYear = "2025"
        url = "https://github.com/yskuem/kmp-onboarding"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "yskuem"
                name = "yskuem"
                url = "https://github.com/yskuem"
            }
        }
        scm {
            url = "https://github.com/yskuem/kmp-onboarding"
            connection = "scm:git:git://github.com/yskuem/kmp-onboarding.git"
            developerConnection = "scm:git:ssh://git@github.com/yskuem/kmp-onboarding.git"
        }
    }
}
