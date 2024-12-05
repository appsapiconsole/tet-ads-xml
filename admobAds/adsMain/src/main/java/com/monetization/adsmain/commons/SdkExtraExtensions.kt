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
import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.controllers.AdsController
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.provider.ad_request.AdRequestProvider
import com.monetization.core.provider.ad_request.DefaultAdRequestProvider
import com.monetization.core.provider.nativeAds.DefaultNativeOptionsProvider
import com.monetization.core.provider.nativeAds.NativeOptionsProvider
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
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null,
    adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
) {
    addNewController(AdmobInterstitialAdsController(adKey, adIdsList, listener, adRequestProvider))
}

fun addNewController(
    adType: AdType,
    adKey: String,
    adIdsList: List<String>,
    bannerAdType: BannerAdType? = null,
    listener: ControllersListener? = null,
    adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
    nativeAdOptionsProvider: NativeOptionsProvider = DefaultNativeOptionsProvider(),
) {
    when (adType) {
        AdType.NATIVE -> AdmobNativeAdsManager.addNewController(
            adKey = adKey,
            adIdsList = adIdsList,
            listener = listener,
            adRequestProvider = adRequestProvider,
            nativeAdOptionsProvider = nativeAdOptionsProvider
        )

        AdType.INTERSTITIAL -> AdmobInterstitialAdsManager.addNewController(
            adKey, adIdsList, listener, adRequestProvider
        )

        AdType.REWARDED -> AdmobRewardedAdsManager.addNewController(adKey, adIdsList, listener)
        AdType.REWARDED_INTERSTITIAL -> AdmobRewardedInterAdsManager.addNewController(
            adKey, adIdsList, listener, adRequestProvider
        )

        AdType.BANNER -> {
            bannerAdType?.let {
                AdmobBannerAdsManager.addNewController(
                    adKey = adKey, adIdsList = adIdsList,
                    bannerAdType = it, listener = listener,
                    adRequestProvider = adRequestProvider
                )
            } ?: run {
                throw IllegalArgumentException("Banner Ad Type Cannot Be Null For Key $adKey")
            }
        }

        AdType.AppOpen -> {
            AdmobAppOpenAdsManager.addNewController(
                adKey = adKey,
                adIdsList = adIdsList,
                listener = listener,
                adRequestProvider = adRequestProvider
            )
        }
    }
}

fun AdmobNativeAdsManager.addNewController(
    adKey: String, adIdsList: List<String>,
    listener: ControllersListener? = null,
    adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
    nativeAdOptionsProvider: NativeOptionsProvider = DefaultNativeOptionsProvider(),
) {
    addNewController(
        AdmobNativeAdsController(
            adKey = adKey,
            adIdsList = adIdsList,
            listener = listener,
            adRequestProvider = adRequestProvider,
            nativeAdOptions = nativeAdOptionsProvider
        )
    )
}


fun AdmobBannerAdsManager.addNewController(
    adKey: String,
    adIdsList: List<String>,
    bannerAdType: BannerAdType = BannerAdType.Normal(BannerAdSize.AdaptiveBanner),
    listener: ControllersListener? = null,
    adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
) {
    addNewController(
        controller = AdmobBannerAdsController(
            adKey = adKey,
            adIdsList = adIdsList,
            bannerAdType = bannerAdType,
            listener = listener,
            adRequestProvider = adRequestProvider
        )
    )
}

fun AdmobAppOpenAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null,
    adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
) {
    addNewController(
        controller = AdmobAppOpenAdsController(
            adKey,
            adIdsList,
            listener,
            adRequestProvider
        )
    )
}


fun AdmobRewardedAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null,
    adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
) {
    addNewController(
        controller = AdmobRewardedAdsController(
            adKey,
            adIdsList,
            listener,
            adRequestProvider
        )
    )
}


fun AdmobRewardedInterAdsManager.addNewController(
    adKey: String, adIdsList: List<String>, listener: ControllersListener? = null,
    adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
) {
    addNewController(AdmobRewardedInterAdsController(adKey, adIdsList, listener, adRequestProvider))
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