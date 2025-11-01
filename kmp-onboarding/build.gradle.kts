import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import java.util.Base64

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)          // v2 系を想定
    alias(libs.plugins.mavenPublish)
    id("signing")
}

// ---- 署名・Central 認証の取得（まず Gradle プロパティ、無ければ env を参照） ----
fun prop(name: String): String? = findProperty(name) as String?
fun env(name: String): String? = providers.environmentVariable(name).orNull

// signingInMemoryKey: Gradleプロパティ優先。無ければ env(GPG_KEY_CONTENTS or GPG_KEY_CONTENTS_B64)
val signingKey: String? =
    prop("signingInMemoryKey")
        ?: env("GPG_KEY_CONTENTS")
        ?: env("GPG_KEY_CONTENTS_B64")?.let { runCatching { String(Base64.getDecoder().decode(it)) }.getOrNull() }

val signingKeyPass: String? =
    prop("signingInMemoryKeyPassword") ?: env("SIGNING_PASSWORD")

val signingKeyId: String? =
    prop("signingInMemoryKeyId") ?: env("SIGNING_KEY_ID")

// Central 認証（User Token）
val centralUser: String? =
    prop("mavenCentralUsername") ?: env("MAVEN_CENTRAL_USERNAME")
val centralPass: String? =
    prop("mavenCentralPassword") ?: env("MAVEN_CENTRAL_PASSWORD")

// Gradle プロパティへ流し込む（Vanniktech も参照できるように）
centralUser?.let { extensions.extraProperties["mavenCentralUsername"] = it }
centralPass?.let { extensions.extraProperties["mavenCentralPassword"] = it }

// ---- KMP ターゲット ----
kotlin {
    androidLibrary {
        namespace = "io.github.yskuem.onboarding"
        compileSdk = 36
        minSdk = 24
        withHostTestBuilder {}
        withDeviceTestBuilder { sourceSetTreeName = "test" }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "kmp-onboardingKit"
    iosX64 { binaries.framework { baseName = xcfName } }
    iosArm64 { binaries.framework { baseName = xcfName } }
    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

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
        commonTest { dependencies { implementation(libs.kotlin.test) } }
        androidMain { }
        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }
        iosMain { }
    }
}

// ---- Maven Central 公開設定 ----
mavenPublishing {
    coordinates("io.github.yskuem", "kmp-onboarding", "1.0.0")

    // 署名は常に有効（pass が null でも可）
    signAllPublications()
    publishToMavenCentral()

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"), // Dokka v2
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


signing {
    isRequired = true
    val key     = signingKey
    val pass    = signingKeyPass
    val keyId   = signingKeyId?.ifBlank { null }
    if (!key.isNullOrBlank()) {
        useInMemoryPgpKeys(keyId, key, pass)
    }
}
