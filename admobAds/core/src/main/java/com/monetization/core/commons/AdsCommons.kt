package com.monetization.core.commons

import android.app.Activity
import android.util.Log
import com.facebook.shimmer.BuildConfig
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.SdkConfigs.isTestMode
import com.monetization.core.controllers.AdsController
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.msgs.MessagesType

object AdsCommons {

    var isDebugMode = BuildConfig.DEBUG

    var adEnabledSdkString = "SDK_TRUE"

    var isFullScreenAdShowing = false
    fun logAds(message: String, isError: Boolean = false) {
        if (isError) {
            Log.e("adsPlugin", "Admob Ads:$message")
        } else {
            Log.d("adsPlugin", "Admob Ads:$message")
        }
    }


    fun Activity.getGoodName(): String {
        return localClassName.substringAfterLast(".")
    }

    fun getAdId(
        indexOfId: Int,
        adIdsList: List<String>,
        adType: AdType,
        newValue: (Int) -> Unit,
    ): String {
        if (indexOfId >= adIdsList.size - 1) {
            newValue.invoke(0)
        } else {
            newValue.invoke(indexOfId + 1)
        }

        val testId = when (adType) {
            AdType.NATIVE -> TestAds.TestNativeId
            AdType.INTERSTITIAL -> TestAds.TestInterId
            AdType.BANNER -> TestAds.TestBannerId
            AdType.AppOpen -> TestAds.TestAppOpenId
            AdType.REWARDED -> TestAds.TestRewardedId
            AdType.REWARDED_INTERSTITIAL -> TestAds.TestRewardedInterId
        }
        return if (isTestMode()) {
            testId
        } else {
            try {
                adIdsList[indexOfId]
            } catch (_: Exception) {
                adIdsList[0]
            }
        }
    }

    fun getShowListener(
        requestNewIfAdShown: Boolean,
        activity: Activity,
        controller: AdsController,
        adType: AdType,
        uiAdsListener: UiAdsListener?,
        showBlackBg: ((Boolean) -> Unit)? = null,
        onRewarded: ((Boolean) -> Unit)? = null,
        onFreeAd: (MessagesType?, Boolean) -> Unit,
    ): FullScreenAdsShowListener {
        return object : FullScreenAdsShowListener {
            override fun onAdShown(adKey: String) {
                super.onAdShown(adKey)
                uiAdsListener?.onImpression(adKey)
            }

            override fun onRewarded(adKey: String) {
                super.onRewarded(adKey)
                uiAdsListener?.onRewarded(adKey)
            }

            override fun onAdClick(adKey: String) {
                super.onAdClick(adKey)
                uiAdsListener?.onAdClicked(adKey)
            }

            override fun onAdShownFailed(adKey: String) {
                super.onAdShownFailed(adKey)
                if (adType == AdType.AppOpen) {
                    showBlackBg?.invoke(false)
                } else if (adType == AdType.REWARDED_INTERSTITIAL || adType == AdType.REWARDED) {
                    onRewarded?.invoke(false)
                }
                onFreeAd.invoke(MessagesType.AdShownFailed, false)
                uiAdsListener?.onFullScreenAdShownFailed(adKey)
            }

            override fun onAdDismiss(adKey: String, adShown: Boolean, rewardEarned: Boolean) {
                if (adType == AdType.AppOpen && adShown) {
                    showBlackBg?.invoke(false)
                } else if (adType == AdType.REWARDED_INTERSTITIAL || adType == AdType.REWARDED) {
                    onRewarded?.invoke(rewardEarned)
                }
                onFreeAd.invoke(null, adShown)
                if (requestNewIfAdShown && adShown) {
                    controller.loadAd(placementKey = adEnabledSdkString, activity, "", null)
                }
            }
        }
    }

}