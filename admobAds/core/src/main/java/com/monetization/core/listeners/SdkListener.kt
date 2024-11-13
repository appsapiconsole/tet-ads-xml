package com.monetization.core.listeners

import com.monetization.core.ad_units.core.AdType

interface SdkListener {
    fun canShowAd(adType: AdType, adKey: String): Boolean
    fun canLoadAd(adType: AdType, adKey: String): Boolean
}