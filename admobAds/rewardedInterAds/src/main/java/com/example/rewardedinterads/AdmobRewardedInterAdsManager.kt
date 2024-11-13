package com.example.rewardedinterads

import com.monetization.core.managers.AdmobBaseAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.TestAds

object AdmobRewardedInterAdsManager :
    AdmobBaseAdsManager<AdmobRewardedInterAdsController>(AdType.REWARDED_INTERSTITIAL) {
    init {
        addNewController(
            AdmobRewardedInterAdsController(
                "Test",
                listOf(TestAds.TestRewardedInterId)
            )
        )
    }

}