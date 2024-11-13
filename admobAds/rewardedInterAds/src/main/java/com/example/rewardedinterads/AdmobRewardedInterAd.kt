package com.example.rewardedinterads

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ad_units.GeneralInterOrAppOpenAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons

class AdmobRewardedInterAd(
    val rewardedInter: RewardedInterstitialAd,
    val adKey: String,
) : GeneralInterOrAppOpenAd {
    private var rewardEarned = false
    override fun showAd(activity: Activity, callBack: FullScreenAdsShowListener) {
        if (AdsCommons.isFullScreenAdShowing) {
            return
        }
        AdsCommons.isFullScreenAdShowing = true
        val controller: AdsControllerBaseHelper? =
            AdmobRewardedInterAdsManager.getAdController(adKey)
        rewardedInter.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                AdsCommons.isFullScreenAdShowing = false
                AdmobRewardedInterAdsManager.getAdController(adKey)?.destroyAd(activity)
                callBack.onAdDismiss(adKey)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                AdmobRewardedInterAdsManager.getAdController(adKey)?.destroyAd(activity)
                callBack.onAdShown(adKey)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                callBack.onAdClick(adKey)
                controller?.onAdClick()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                controller?.onImpression()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                AdsCommons.isFullScreenAdShowing = false
                callBack.onAdDismiss(adKey = adKey, adShown = true, rewardEarned = rewardEarned)
                controller?.onDismissed()
            }
        }
        rewardedInter.show(
            activity
        ) { rewardEarned = true }
    }

    override fun destroyAd() {
    }


    override fun getAdType(): AdType {
        return AdType.REWARDED_INTERSTITIAL
    }
}