package com.example.adsxml.ads

import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.core.ad_units.core.AdType

enum class AdKeys(val adType: AdType, val adIds: List<String>, val bannerAdType: BannerAdType? = null) {
    Test(adType = AdType.AppOpen, adIds = listOf("")),
    MainBanner(adType = AdType.BANNER, adIds = listOf(""), bannerAdType = BannerAdType.Normal(BannerAdSize.AdaptiveBanner)),
}