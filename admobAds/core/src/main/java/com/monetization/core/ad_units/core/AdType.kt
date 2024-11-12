package com.monetization.core.ad_units.core

import com.monetization.core.commons.TestAds

enum class AdType(val isFullScreenAds: Boolean = false) {
    NATIVE,
    INTERSTITIAL(true),
    REWARDED(true),
    REWARDED_INTERSTITIAL(true),
    BANNER,
    AppOpen(true)
}

fun AdType.getTestAdId(): String {
    return when (this) {
        AdType.NATIVE -> TestAds.TestNativeId
        AdType.INTERSTITIAL -> TestAds.TestInterId
        AdType.BANNER -> TestAds.TestBannerId
        AdType.AppOpen -> TestAds.TestAppOpenId
        AdType.REWARDED -> TestAds.TestRewardedId
        AdType.REWARDED_INTERSTITIAL -> TestAds.TestRewardedInterId
    }
}