import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import java.util.Base64

plugins {
    id("com.android.library")          // android { } を使うため必須
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    signing
}

// -------------------- Signing: Base64鍵のみを使用 --------------------
val signingKeyId = providers.environmentVariable("SIGNING_KEY_ID").orNull
val signingKeyPassword = providers.environmentVariable("SIGNING_PASSWORD").orNull
val signingKeyBase64 = providers.environmentVariable("GPG_KEY_CONTENTS_B64").orNull
val asciiArmoredKey: String? = signingKeyBase64?.let { runCatching {
    String(Base64.getDecoder().decode(it))
}.getOrNull() }

// vanniktech のドキュメントが想定するプロパティ名（保険として設定）
asciiArmoredKey?.let { extra["signingInMemoryKey"] = it }
signingKeyId?.let { extra["signingInMemoryKeyId"] = it }
signingKeyPassword?.let { extra["signingInMemoryKeyPassword"] = it }

// Sonatype 認証（ユーザートークン）
providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull?.let { extra["mavenCentralUsername"] = it }
providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull?.let { extra["mavenCentralPassword"] = it }

// Gradle Signing プラグインに確実に鍵を登録
signing {
    if (asciiArmoredKey != null && signingKeyPassword != null) {
        useInMemoryPgpKeys(signingKeyId, asciiArmoredKey, signingKeyPassword)
    } else {
        logger.warn("Signing key not configured. Publications will not be signed.")
    }
}

kotlin {
    // Android ターゲット
    androidTarget {
        // ライブラリとして公開するビルド変種
        publishLibraryVariants("release")
    }

    // iOS ターゲット
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
        val iosMain by creating
        val iosX64Main by getting; val iosArm64Main by getting; val iosSimulatorArm64Main by getting
        iosX64Main.dependsOn(iosMain); iosArm64Main.dependsOn(iosMain); iosSimulatorArm64Main.dependsOn(iosMain)
    }
}

// Android 側設定（AAR の sources.jar を必ず生成）
android {
    namespace = "io.github.yskuem.onboarding"
    compileSdk = 36
    defaultConfig { minSdk = 24 }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

// -------------------- Maven Central 公開設定 --------------------
mavenPublishing {
    // グループID: io.github.yskuem, アーティファクト: kmp-onboarding, バージョン: 1.0.0
    coordinates("io.github.yskuem", "kmp-onboarding", "1.0.0")

    // Central Portal をターゲットにし、全 publication を署名
    publishToMavenCentral()
    signAllPublications()  // ← これが無いと .asc が生成されません（公式手順）

    // KMP では空JavadocJarで要件を満たすのが安定
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
