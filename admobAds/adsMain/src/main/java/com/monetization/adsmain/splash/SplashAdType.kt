package com.monetization.adsmain.splash

/*enum class SplashAdType() {
    None,
    AdmobInter,
    AdmobAppOpen
}*/
sealed class SplashAdType {
    data object None : SplashAdType()
    data class AdmobInter(val key: String) : SplashAdType()
    data class AdmobAppOpen(val key: String) : SplashAdType()
}

fun SplashAdType.getAdKey(): String {
    return when (this) {
        is SplashAdType.AdmobInter -> {
            this.key
        }

        is SplashAdType.AdmobAppOpen -> {
            this.key
        }

        SplashAdType.None -> {
            "None"
        }
    }
}