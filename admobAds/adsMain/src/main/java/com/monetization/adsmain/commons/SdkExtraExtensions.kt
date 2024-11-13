package com.monetization.adsmain.commons

import android.app.Activity
import com.example.rewadedad.AdmobRewardedAdsController
import com.example.rewadedad.AdmobRewardedAdsManager
import com.example.rewardedinterads.AdmobRewardedInterAdsController
import com.example.rewardedinterads.AdmobRewardedInterAdsManager
import com.monetization.appopen.AdmobAppOpenAdsController
import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.bannerads.AdmobBannerAdsController
import com.monetization.bannerads.AdmobBannerAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.adEnabledSdkString
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.controllers.AdsController
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.managers.AdsManager
import com.monetization.interstitials.AdmobInterstitialAdsController
import com.monetization.interstitials.AdmobInterstitialAdsManager
import com.monetization.nativeads.AdmobNativeAdsController
import com.monetization.nativeads.AdmobNativeAdsManager


fun String.isAdRequesting(adType: AdType): Boolean {
    return getAdController(adType)?.isAdRequesting() ?: false
}

fun String.loadAd(
    placementKey: String,
    activity: Activity,
    adType: AdType,
    listener: AdsLoadingStatusListener? = null
) {
    getAdController(adType)?.loadAd(placementKey, activity, "", listener)
}

fun String.errorLogging() {
    logAds(
        message = """
                                    *********************************************************
                                    *********************************************************
                                    -------------------------Hi------------------------------
                                    --$this--
                                    *********************************************************
                                    *********************************************************
                                """.trimIndent(),
        isError = true
    )
}

fun String.destroyAd(adType: AdType, activity: Activity? = null) {
    getAdController(adType)?.destroyAd(activity)
}

fun AdType.loadAd(
    placementKey: String,
    key: String,
    activity: Activity,
    listener: AdsLoadingStatusListener? = null
) {
    getAdController(key)?.loadAd(placementKey, activity, "", listener)
}


fun String.isAdAvailable(adType: AdType): Boolean {
    return getAdController(adType)?.isAdAvailable() ?: false
}

fun AdType.getAvailableAdsControllers(): List<AdsController> {
    return getAllControllers().filter { it.isAdAvailable() }
}

fun AdType.getRequestingControllers(): List<AdsController> {
    return getAllControllers().filter { it.isAdRequesting() }
}

fun AdType.getAvailableOrRequestingControllers(): List<AdsController> {
    return getAllControllers().filter { it.isAdAvailableOrRequesting() }
}

fun String.isAdAvailableOrRequesting(adType: AdType): Boolean {
    return getAdController(adType)?.isAdAvailableOrRequesting() ?: false
}

fun String.getAdIdToRequest(adType: AdType): String? {
    return getAdController(adType)?.getAdId()
}

fun String.getAdController(adType: AdType): AdsController? {
    return adType.getAdController(this)
}

fun AdmobInterstitialAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null
) {
    addNewController(AdmobInterstitialAdsController(adKey, adIdsList, listener))
}

fun addNewController(
    adType: AdType, adKey: String, adIdsList: List<String>, listener: ControllersListener? = null
) {
    when (adType) {
        AdType.NATIVE -> AdmobNativeAdsManager.addNewController(adKey, adIdsList, listener)
        AdType.INTERSTITIAL -> AdmobInterstitialAdsManager.addNewController(
            adKey, adIdsList, listener
        )

        AdType.REWARDED -> AdmobRewardedAdsManager.addNewController(adKey, adIdsList, listener)
        AdType.REWARDED_INTERSTITIAL -> AdmobRewardedInterAdsManager.addNewController(
            adKey, adIdsList, listener
        )

        AdType.BANNER -> AdmobBannerAdsManager.addNewController(adKey, adIdsList, listener)
        AdType.AppOpen -> AdmobAppOpenAdsManager.addNewController(adKey, adIdsList, listener)
    }
}

fun AdmobNativeAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null
) {
    addNewController(AdmobNativeAdsController(adKey, adIdsList, listener))
}


fun AdmobBannerAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null
) {
    addNewController(AdmobBannerAdsController(adKey, adIdsList, listener))
}

fun AdmobAppOpenAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null
) {
    addNewController(AdmobAppOpenAdsController(adKey, adIdsList, listener))
}


fun AdmobRewardedAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null
) {
    addNewController(AdmobRewardedAdsController(adKey, adIdsList, listener))
}


fun AdmobRewardedInterAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null
) {
    addNewController(AdmobRewardedInterAdsController(adKey, adIdsList, listener))
}


fun AdType.getAdController(key: String): AdsController? {
    val manager = when (this) {
        AdType.NATIVE -> AdmobNativeAdsManager
        AdType.INTERSTITIAL -> AdmobInterstitialAdsManager
        AdType.REWARDED -> AdmobRewardedAdsManager
        AdType.REWARDED_INTERSTITIAL -> AdmobRewardedInterAdsManager
        AdType.BANNER -> AdmobBannerAdsManager
        AdType.AppOpen -> AdmobAppOpenAdsManager
    }
    return manager.getAdController(key)
}

fun AdType.getAllControllers(): List<AdsController> {
    return when (this) {
        AdType.NATIVE -> AdmobNativeAdsManager.getAllController()
        AdType.INTERSTITIAL -> AdmobInterstitialAdsManager.getAllController()
        AdType.REWARDED -> AdmobRewardedAdsManager.getAllController()
        AdType.REWARDED_INTERSTITIAL -> AdmobRewardedInterAdsManager.getAllController()
        AdType.BANNER -> AdmobBannerAdsManager.getAllController()
        AdType.AppOpen -> AdmobAppOpenAdsManager.getAllController()
    }
}