package lib.yskuem.kmp.onboarding

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform