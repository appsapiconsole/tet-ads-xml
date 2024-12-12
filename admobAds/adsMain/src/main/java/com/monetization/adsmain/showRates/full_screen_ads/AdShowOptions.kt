package com.monetization.adsmain.showRates.full_screen_ads

enum class AdShowOptions {
    InstantAd,
    ShowOnlyIfAvailable,
    InstantIfRequesting,
    InstantIfNotAvailable,
}

fun Boolean.toInstantAd(): AdShowOptions {
    return AdShowOptions.InstantAd
}

fun Boolean.toPreloadAd(): AdShowOptions {
    return AdShowOptions.ShowOnlyIfAvailable
}

fun Boolean.toInstantAdIfAlreadyRequesting(): AdShowOptions {
    return AdShowOptions.InstantIfRequesting
}

fun Boolean.toInstantAdIfNotAvailable(): AdShowOptions {
    return AdShowOptions.InstantIfNotAvailable
}