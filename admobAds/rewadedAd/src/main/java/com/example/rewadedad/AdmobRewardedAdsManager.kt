package com.example.rewadedad

import com.monetization.core.managers.AdmobBaseAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.TestAds

object AdmobRewardedAdsManager : AdmobBaseAdsManager<AdmobRewardedAdsController>(AdType.REWARDED) {

    init {
        addNewController(AdmobRewardedAdsController("Test", listOf(TestAds.TestRewardedId)))
    }

}