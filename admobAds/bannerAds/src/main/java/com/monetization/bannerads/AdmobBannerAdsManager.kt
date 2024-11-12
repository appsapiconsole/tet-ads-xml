package com.monetization.bannerads

import com.monetization.core.managers.AdmobBaseAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.TestAds

object AdmobBannerAdsManager : AdmobBaseAdsManager<AdmobBannerAdsController>(AdType.NATIVE) {
    init {
        addNewController(AdmobBannerAdsController("Test", listOf(TestAds.TestBannerId)))
    }
}





















