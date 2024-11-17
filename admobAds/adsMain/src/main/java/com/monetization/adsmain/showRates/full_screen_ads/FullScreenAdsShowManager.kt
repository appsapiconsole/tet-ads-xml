package com.monetization.adsmain.showRates.full_screen_ads

import android.app.Activity
import com.example.rewadedad.extensions.counter.InstantCounterRewardedAdsManager
import com.example.rewardedinterads.extensions.counter.PreloadCounterRewInterManager
import com.monetization.adsmain.commons.errorLogging
import com.monetization.adsmain.commons.getAdTypeByKey
import com.monetization.appopen.extension.InstantAppOpenAdsManager
import com.monetization.appopen.extension.PreloadAppOpenAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType
import com.monetization.interstitials.extensions.counter.InstantCounterInterAdsManager
import com.monetization.interstitials.extensions.counter.PreloadCounterInterAdsManager

object FullScreenAdsShowManager {

    fun showFullScreenAd(
        placementKey: String,
        key: String,
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
        activity: Activity,
        isInstantAd: Boolean = false,
        uiAdsListener: UiAdsListener? = null,
        adType: AdType,
        requestNewIfNotAvailable: Boolean = false,
        requestNewIfAdShown: Boolean = false,
        normalLoadingTime: Long = 1_000,
        instantLoadingTime: Long = 8_000,
        counterKey: String? = null,
        onRewarded: ((Boolean) -> Unit)? = null,
        onCounterUpdate: ((Int) -> Unit)? = null,
    ) {
        when (adType) {
            AdType.INTERSTITIAL -> {
                if (isInstantAd) {
                    InstantCounterInterAdsManager.showInstantInterstitialAd(
                        placementKey = placementKey,
                        activity = activity,
                        key = key,
                        counterKey = counterKey,
                        uiAdsListener = uiAdsListener,
                        normalLoadingTime = normalLoadingTime,
                        instantLoadingTime = instantLoadingTime,
                        requestNewIfAdShown = requestNewIfAdShown,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        onCounterUpdate = onCounterUpdate
                    )
                } else {
                    PreloadCounterInterAdsManager.tryShowingInterstitialAd(
                        placementKey = placementKey,
                        key = key,
                        counterKey = counterKey,
                        activity = activity,
                        uiAdsListener = uiAdsListener,
                        requestNewIfNotAvailable = requestNewIfNotAvailable,
                        requestNewIfAdShown = requestNewIfAdShown,
                        normalLoadingTime = normalLoadingTime,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        onCounterUpdate = onCounterUpdate
                    )
                }
            }

            AdType.REWARDED -> {
                if (isInstantAd) {
                    InstantCounterInterAdsManager.showInstantInterstitialAd(
                        placementKey = placementKey,
                        activity = activity,
                        key = key,
                        counterKey = counterKey,
                        uiAdsListener = uiAdsListener,
                        normalLoadingTime = normalLoadingTime,
                        instantLoadingTime = instantLoadingTime,
                        requestNewIfAdShown = requestNewIfAdShown,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        onCounterUpdate = onCounterUpdate
                    )
                } else {
                    PreloadCounterInterAdsManager.tryShowingInterstitialAd(
                        placementKey = placementKey,
                        key = key,
                        counterKey = counterKey,
                        activity = activity,
                        uiAdsListener = uiAdsListener,
                        requestNewIfNotAvailable = requestNewIfNotAvailable,
                        requestNewIfAdShown = requestNewIfAdShown,
                        normalLoadingTime = normalLoadingTime,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        onCounterUpdate = onCounterUpdate
                    )
                }
            }

            AdType.REWARDED_INTERSTITIAL -> {
                if (isInstantAd) {
                    InstantCounterRewardedAdsManager.showInstantRewardedAd(
                        placementKey = placementKey,
                        activity = activity,
                        key = key,
                        counterKey = counterKey,
                        uiAdsListener = uiAdsListener,
                        normalLoadingTime = normalLoadingTime,
                        instantLoadingTime = instantLoadingTime,
                        requestNewIfAdShown = requestNewIfAdShown,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        onRewarded = {
                            onRewarded?.invoke(it)
                        },
                        onCounterUpdate = onCounterUpdate
                    )
                } else {
                    PreloadCounterRewInterManager.tryShowingRewardedInterAd(
                        placementKey = placementKey,
                        key = key,
                        counterKey = counterKey,
                        activity = activity,
                        uiAdsListener = uiAdsListener,
                        requestNewIfNotAvailable = requestNewIfNotAvailable,
                        requestNewIfAdShown = requestNewIfAdShown,
                        normalLoadingTime = normalLoadingTime,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        onRewarded = {
                            onRewarded?.invoke(it)
                        },
                        onCounterUpdate = onCounterUpdate
                    )
                }
            }

            AdType.AppOpen -> {
                if (isInstantAd) {
                    InstantAppOpenAdsManager.showInstantAppOpenAd(
                        placementKey = placementKey,
                        activity = activity,
                        key = key,
                        uiAdsListener = uiAdsListener,
                        normalLoadingTime = normalLoadingTime,
                        instantLoadingTime = instantLoadingTime,
                        requestNewIfAdShown = requestNewIfAdShown,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        showBlackBg = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = true
                            )
                        }
                    )
                } else {
                    PreloadAppOpenAdsManager.tryShowingAppOpenAd(
                        placementKey = placementKey,
                        key = key,
                        activity = activity,
                        requestNewIfNotAvailable = requestNewIfNotAvailable,
                        requestNewIfAdShown = requestNewIfAdShown,
                        normalLoadingTime = normalLoadingTime,
                        uiAdsListener = uiAdsListener,
                        onLoadingDialogStatusChange = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = false
                            )
                        },
                        onAdDismiss = onAdDismiss,
                        showBlackBg = {
                            SdkConfigs.getSdkDialogListener()?.onAdLoadingDialogStateChange(
                                activity = activity,
                                placementKey = placementKey,
                                adKey = key,
                                adType = adType,
                                showDialog = it,
                                isForBlackBg = true
                            )
                        }
                    )
                }
            }

            else -> {
                logAds("AdType:$adType is not a full screen ad", true)
            }
        }
    }
}