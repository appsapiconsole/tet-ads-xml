package com.monetization.adsmain.showRates.inter

import android.app.Activity
import com.monetization.adsmain.commons.isAdAvailable
import com.monetization.adsmain.commons.isAdAvailableOrRequesting
import com.monetization.adsmain.commons.isAdRequesting
import com.monetization.adsmain.showRates.ShowRatesHelper
import com.monetization.adsmain.showRates.full_screen_ads.FullScreenAdsShowManager.showFullScreenAd
import com.monetization.adsmain.showRates.models.IgnoreAfterEffects
import com.monetization.adsmain.showRates.models.IgnoreNewRequest
import com.monetization.adsmain.showRates.models.ignoreIfAnyOtherRequestingOrLoadedAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.msgs.MessagesType
import com.monetization.core.showRates.ShowRatesCommons

val showRatesHelper = ShowRatesHelper()
/*

object ShowRateFullScreenAds {

    fun showFullScreenAdsWithSR(
        placementKey: String,
        activity: Activity,
        key: String,
        counterKey: String? = null,
        adType: AdType,
        normalLoadingTime: Long = 1_000,
        instantLoadingTime: Long = 8_000,
        isInstantAd: Boolean = false,
        ignoreKeysFromLimitCount: List<String> = listOf(),
        ignoreNewRequest: IgnoreNewRequest = ignoreIfAnyOtherRequestingOrLoadedAd(adType),
        requestNewIfNotAvailable: Boolean = false,
        requestNewIfAdShown: Boolean = false,
        onRewarded: ((Boolean) -> Unit)? = null,
        showBlackBg: ((Boolean) -> Unit)? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onAdDismiss: (Boolean) -> Unit,
    ) {
        var canRequestRequestNewIfNotAvailable = true
        var canRequestRequestNewIfAdShown = true
        val newOnAdDismiss: (Boolean, MessagesType?) -> Unit = { adShown ,msg->
            onAdDismiss.invoke(adShown)
            if (adShown) {
                if (requestNewIfAdShown && canRequestRequestNewIfAdShown) {
                    key.loadAdWithSR(adType, activity, ignoreFromLimit = ignoreKeysFromLimitCount)
                }
            } else {
//                Only For Preloads
                if (requestNewIfNotAvailable && canRequestRequestNewIfNotAvailable) {
                    key.loadAdWithSR(adType, activity, ignoreFromLimit = ignoreKeysFromLimitCount)
                }
            }
        }
        val alreadyLoadedOrRequesting =
            showRatesHelper.getLoadedOrRequestingAdsControllersByType(adType)
        val adAvailable = key.isAdAvailable(adType)
        val adRequesting = key.isAdRequesting(adType)

        val adsWillingToBeUsed = when (ignoreNewRequest) {
            IgnoreNewRequest.DontIgnore -> {
                listOf()
            }

            is IgnoreNewRequest.IfTheseAvailable -> {
                ignoreNewRequest.ids.filter {
                    it.isAdAvailable(adType)
                }
            }

            is IgnoreNewRequest.IfTheseRequesting -> {
                ignoreNewRequest.ids.filter {
                    it.isAdRequesting(adType)
                }
            }

            is IgnoreNewRequest.IfTheseRequestingOrLoaded -> {
                ignoreNewRequest.ids.filter {
                    it.isAdAvailableOrRequesting(adType)
                }
            }
        }
        if (adAvailable || adRequesting) {
            logAds("Ad It self is available=${adAvailable},requesting=${adRequesting} key=$key")
            showFullScreenAd(
                placementKey = placementKey,
                activity = activity,
                key = key,
                counterKey = counterKey,
                adType = adType,
                isInstantAd = isInstantAd,
                normalLoadingTime = normalLoadingTime,
                instantLoadingTime = instantLoadingTime,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                onRewarded = onRewarded,
                showBlackBg = showBlackBg,
                onAdDismiss = newOnAdDismiss
            )
        } else if (adsWillingToBeUsed.isNotEmpty()) {
            val newKey = adsWillingToBeUsed[0]
            logAds("Ad($key) Not Available but willing to be used $newKey is attached")
            when (ignoreNewRequest.afterLoadingEffects) {
                IgnoreAfterEffects.IgnoreRequestNewIfAdShown -> {
                    canRequestRequestNewIfAdShown = false
                    canRequestRequestNewIfNotAvailable = true
                }

                IgnoreAfterEffects.IgnoreRequestNewIfNotAvailable -> {
                    canRequestRequestNewIfNotAvailable = false
                    canRequestRequestNewIfAdShown = true
                }

                IgnoreAfterEffects.IgnoreBothCalls -> {
                    canRequestRequestNewIfAdShown = false
                    canRequestRequestNewIfNotAvailable = false

                }

                IgnoreAfterEffects.DontIgnoreBeNormal -> {
                    canRequestRequestNewIfAdShown = true
                    canRequestRequestNewIfNotAvailable = true
                }
            }

            showFullScreenAd(
                placementKey = placementKey,
                activity = activity,
                key = newKey,
                counterKey = counterKey,
                adType = adType,
                isInstantAd = isInstantAd,
                normalLoadingTime = normalLoadingTime,
                instantLoadingTime = instantLoadingTime,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                onRewarded = onRewarded,
                showBlackBg = showBlackBg,
                onAdDismiss = newOnAdDismiss
            )
        } else if (ShowRatesCommons.getBestShowRates().canRequestNewAd(
                adType = adType, size = alreadyLoadedOrRequesting.filter {
                    ignoreKeysFromLimitCount.contains(it.getAdKey()).not()
                }.size
            )
        ) {
            logAds("Ad Not Available but requesting new")
            showFullScreenAd(
                placementKey = placementKey,
                activity = activity,
                key = key,
                counterKey = counterKey,
                adType = adType,
                isInstantAd = isInstantAd,
                normalLoadingTime = normalLoadingTime,
                instantLoadingTime = instantLoadingTime,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                onRewarded = onRewarded,
                showBlackBg = showBlackBg,
                onAdDismiss = newOnAdDismiss
            )
        } else {
            logAds("Show Rates Factor Denied to do anything", true)
            onAdDismiss.invoke(false)
        }
    }
}*/
