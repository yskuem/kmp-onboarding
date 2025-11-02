import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import java.util.Base64

plugins {
    id("com.android.library") // ← これが無いと android{ } が解決しない
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    signing
}

// ---- Signing（B64鍵のみ）----
val signingKeyId = providers.environmentVariable("SIGNING_KEY_ID").orNull
val signingKeyPassword = providers.environmentVariable("SIGNING_PASSWORD").orNull
val signingKeyBase64 = providers.environmentVariable("GPG_KEY_CONTENTS_B64").orNull
val resolvedSigningKey = signingKeyBase64?.let { encoded ->
    runCatching { String(Base64.getDecoder().decode(encoded)) }.getOrNull()
}

// vanniktech が参照する extra property（保険）
resolvedSigningKey?.let { extra["signingInMemoryKey"] = it }
signingKeyPassword?.let { extra["signingInMemoryKeyPassword"] = it }
signingKeyId?.let { extra["signingInMemoryKeyId"] = it }

// Central 認証
providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull?.let {
    extra["mavenCentralUsername"] = it
}
providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull?.let {
    extra["mavenCentralPassword"] = it
}

// すべての publication を署名
signing {
    if (resolvedSigningKey != null && signingKeyPassword != null) {
        useInMemoryPgpKeys(signingKeyId, resolvedSigningKey, signingKeyPassword)
        sign(publishing.publications)
    } else {
        logger.warn("Signing key not configured. Publications will not be signed.")
    }
}

kotlin {
    // ← ここは androidLibrary ではなく androidTarget
    androidTarget {
        // ライブラリとして公開する変種
        publishLibraryVariants("release")
    }

    val xcfName = "kmp-onboardingKit"
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
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
        val commonTest by getting { dependencies { implementation(libs.kotlin.test) } }

        val androidMain by getting

        // iOS 共通ソースセット（任意）
        val iosMain by creating
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        iosX64Main.dependsOn(iosMain)
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)
    }
}

// Android 設定はトップレベルの android{ } に置く
android {
    namespace = "io.github.yskuem.onboarding"
    compileSdk = 36
    defaultConfig { minSdk = 24 }

    // AAR の sources.jar を必ず生成
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

mavenPublishing {
    coordinates("io.github.yskuem", "kmp-onboarding", "1.0.0")

    // 引数なしで Central Portal（新仕様）
    publishToMavenCentral()
    signAllPublications()

    // KMP の javadoc 要件は空Jarで満たす（Dokkaタスク名依存を避ける）
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
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
