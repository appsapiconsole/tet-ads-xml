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
        /*if (AdsCommons.isFullScreenAdShowing) {
            return
        }*/
        AdsCommons.isFullScreenAdShowing = true
        val controller: AdsControllerBaseHelper? =
            AdmobRewardedInterAdsManager.getAdController(adKey)
        rewardedInter.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                AdsCommons.isFullScreenAdShowing = false
                controller?.onFailToShow()
                callBack.onAdShownFailed(adKey)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                AdmobRewardedInterAdsManager.getAdController(adKey)?.destroyAd(activity)
                controller?.onAdShown()
                callBack.onAdShown(adKey)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                controller?.onAdClick()
                callBack.onAdClick(adKey)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                controller?.onImpression()
                callBack.onAdImpression(adKey)
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                AdsCommons.isFullScreenAdShowing = false
                controller?.onDismissed()
                callBack.onAdDismiss(adKey = adKey, adShown = true, rewardEarned = rewardEarned)
            }
        }
        callBack.onAdAboutToShow(adKey)
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