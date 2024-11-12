package com.monetization.interstitials

import com.monetization.core.managers.AdmobBaseAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.TestAds

object AdmobInterstitialAdsManager :
    AdmobBaseAdsManager<AdmobInterstitialAdsController>(AdType.INTERSTITIAL) {

    init {
        addNewController(AdmobInterstitialAdsController("Test", listOf(TestAds.TestInterId)))
    }

}