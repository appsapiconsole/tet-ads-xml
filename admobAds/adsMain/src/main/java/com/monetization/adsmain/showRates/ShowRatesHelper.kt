package com.monetization.adsmain.showRates

import com.monetization.bannerads.AdmobBannerAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.controllers.AdsController
import com.monetization.core.showRates.ShowRatesCommons
import com.monetization.interstitials.AdmobInterstitialAdsManager
import com.monetization.nativeads.AdmobNativeAdsManager

class ShowRatesHelper {


    fun canRequestNewAd(
        adType: AdType,
        ignoreFromList: List<String> = emptyList()
    ): Boolean {
        val loadedAds = getLoadedOrRequestingAdsControllersByType(adType).filter {
            ignoreFromList.contains(it.getAdKey()).not()
        }
        return isLimitNotExceeded(adType, loadedAds.size)
    }

    fun getLoadedAdsControllersByType(adType: AdType) = getAllControllersList().filter {
        it.isAdAvailable() && it.getAdType() == adType
    }

    fun getRequestingAdsControllersByType(adType: AdType) = getAllControllersList().filter {
        it.isAdRequesting() && it.getAdType() == adType
    }

    fun getLoadedOrRequestingAdsControllersByType(
        adType: AdType,
        ignoreFromList: List<String> = listOf()
    ) = getAllControllersList().filter {
        it.isAdAvailableOrRequesting() && it.getAdType() == adType && ignoreFromList.contains(it.getAdKey())
            .not()
    }.sortedBy { it.isAdAvailable() }

    fun getLoadedAdsControllers() = getAllControllersList().filter {
        it.isAdAvailable()
    }

    fun getNativeControllersWithHistory() = getAllControllersList().filter {
        it.getAdType() == AdType.NATIVE && it.getHistory().isNotEmpty()
    }

    fun getRequestingAdsControllers() = getAllControllersList().filter {
        it.isAdRequesting()
    }

    fun isLimitNotExceeded(adType: AdType, loadedOrRequestingSize: Int): Boolean {
        return ShowRatesCommons.getBestShowRates()
            .canRequestNewAd(adType, loadedOrRequestingSize)
    }

    private fun getAllControllersList(): List<AdsController> {
        return AdmobNativeAdsManager.getAllController() +
                AdmobBannerAdsManager.getAllController() +
                AdmobInterstitialAdsManager.getAllController()
    }

}