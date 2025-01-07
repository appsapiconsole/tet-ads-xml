package com.monetization.adsmain.showRates.full_screen_ads

enum class AdShowOptions {
    ShowIfAvailable,
    InstantIfRequesting,
    Instant,
}

fun Boolean.toInstantAd(): AdShowOptions {
    return AdShowOptions.Instant
}

fun Boolean.toPreloadAd(): AdShowOptions {
    return AdShowOptions.ShowIfAvailable
}

fun Boolean.toInstantAdIfAlreadyRequesting(): AdShowOptions {
    return AdShowOptions.InstantIfRequesting
}