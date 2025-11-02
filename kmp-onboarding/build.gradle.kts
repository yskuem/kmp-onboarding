import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import java.util.Base64

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    signing
}

// ---------- Signing inputs (env または -P の両対応) ----------
val keyB64: String? =
    providers.environmentVariable("GPG_KEY_CONTENTS_B64").orNull
        ?: providers.gradleProperty("signingInMemoryKeyB64").orNull

// keyArmored は ASCII-armored PRIVATE KEY 本体
val keyArmored: String? = when {
    keyB64 != null -> runCatching { String(Base64.getDecoder().decode(keyB64)) }.getOrNull()
    else -> providers.gradleProperty("signingInMemoryKey").orNull
}

val keyPass: String? =
    providers.environmentVariable("SIGNING_PASSWORD").orNull
        ?: providers.gradleProperty("signingInMemoryKeyPassword").orNull

// vanniktech が見る extra（保険）
keyArmored?.let { extra["signingInMemoryKey"] = it }
keyPass?.let { extra["signingInMemoryKeyPassword"] = it }

// Sonatype 認証
providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull?.let { extra["mavenCentralUsername"] = it }
providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull?.let { extra["mavenCentralPassword"] = it }

// Gradle Signing に鍵を登録＋全 publication を署名（keyId は渡さない）
signing {
    if (keyArmored != null && keyPass != null) {
        useInMemoryPgpKeys(keyArmored, keyPass) // ← 2引数版
        sign(publishing.publications)
    } else {
        logger.warn("Signing key not configured. Publications will not be signed.")
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
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
        // Default Hierarchy を使うため iosMain は明示作成しない
        val androidMain by getting
    }
}

android {
    namespace = "io.github.yskuem.onboarding"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    publishing {
        singleVariant("release") { withSourcesJar() }
    }
}

mavenPublishing {
    coordinates("io.github.yskuem", "kmp-onboarding", "1.0.1") // 1.0.0 に未署名がある場合は上げる
    publishToMavenCentral()
    signAllPublications()

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
            developer { id = "yskuem"; name = "yskuem"; url = "https://github.com/yskuem" }
        }
        scm {
            url = "https://github.com/yskuem/kmp-onboarding"
            connection = "scm:git:git://github.com/yskuem/kmp-onboarding.git"
            developerConnection = "scm:git:ssh://git@github.com/yskuem/kmp-onboarding.git"
        }
    }
}
