package com.monetization.adsmain.manager

import com.example.rewadedad.AdmobRewardedAdsManager
import com.example.rewardedinterads.AdmobRewardedInterAdsManager
import com.monetization.adsmain.commons.addNewController
import com.monetization.adsmain.commons.getAdController
import com.monetization.adsmain.commons.getAllControllers
import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.bannerads.AdmobBannerAdsManager
import com.monetization.bannerads.BannerAdType
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.controllers.AdsController
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsManager
import com.monetization.interstitials.AdmobInterstitialAdsManager
import com.monetization.nativeads.AdmobNativeAdsManager

object AdmobAdsManager {

    fun getAdController(
        adType: AdType,
        adKey: String
    ): AdsController? {
        return adType.getAdController(adKey)
    }

    fun getAllController(adType: AdType): List<AdsController> {
        return adType.getAllControllers()
    }

    fun addNewController(
        adType: AdType,
        adKey: String,
        adIdsList: List<String>,
        bannerAdType: BannerAdType? = null,
        listener: ControllersListener? = null
    ) {
        AdmobNativeAdsManager.getAllController()
        when (adType) {
            AdType.NATIVE -> AdmobNativeAdsManager.addNewController(adKey, adIdsList, listener)
            AdType.INTERSTITIAL -> AdmobInterstitialAdsManager.addNewController(
                adKey, adIdsList, listener
            )

            AdType.REWARDED -> AdmobRewardedAdsManager.addNewController(adKey, adIdsList, listener)
            AdType.REWARDED_INTERSTITIAL -> AdmobRewardedInterAdsManager.addNewController(
                adKey, adIdsList, listener
            )

            AdType.BANNER -> {
                bannerAdType?.let {
                    AdmobBannerAdsManager.addNewController(
                        adKey = adKey, adIdsList = adIdsList,
                        bannerAdType = it, listener = listener
                    )
                } ?: run {
                    throw IllegalArgumentException("Banner Ad Type Cannot Be Null For Key $adKey")
                }
            }

            AdType.AppOpen -> AdmobAppOpenAdsManager.addNewController(adKey, adIdsList, listener)
        }
    }
}