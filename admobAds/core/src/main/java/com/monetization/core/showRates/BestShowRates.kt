package com.monetization.core.showRates

import com.monetization.core.ad_units.core.AdType

data class BestShowRates(
    val maxNativeRequestAtTime: Int = 2,
    val maxInterRequestAtTime: Int = 1,
    val maxAppOpenRequestAtTime: Int = 1,
    val maxBannerRequestAtTime: Int = 1,
) {
    fun canRequestNewAd(adType: AdType, size: Int): Boolean {
        return when (adType) {
            AdType.NATIVE -> size < maxNativeRequestAtTime
            AdType.INTERSTITIAL -> size < maxInterRequestAtTime
            AdType.BANNER -> size < maxBannerRequestAtTime
            AdType.AppOpen -> size < maxAppOpenRequestAtTime
            else -> {
                true
            }
        }
    }
}
