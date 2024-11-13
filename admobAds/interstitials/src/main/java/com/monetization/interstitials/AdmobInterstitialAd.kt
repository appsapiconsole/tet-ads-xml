package com.monetization.interstitials

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ad_units.GeneralInterOrAppOpenAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.commons.AdsCommons.logAds

class AdmobInterstitialAd(
    val interstitialAds: InterstitialAd,
    val adKey: String,
) : GeneralInterOrAppOpenAd {


    override fun showAd(activity: Activity, callBack: FullScreenAdsShowListener) {
        if (AdsCommons.isFullScreenAdShowing) {
            return
        }
        AdsCommons.isFullScreenAdShowing = true
        val controller: AdsControllerBaseHelper? = AdmobInterstitialAdsManager.getAdController(adKey)
        interstitialAds.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                AdsCommons.isFullScreenAdShowing = false
                AdmobInterstitialAdsManager.getAdController(adKey)?.destroyAd(activity)
                callBack.onAdDismiss(adKey)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                AdmobInterstitialAdsManager.getAdController(adKey)?.destroyAd(activity)
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
                logAds("Interstitial onAdDismissedFullScreenContent called", true)
                AdsCommons.isFullScreenAdShowing = false
                callBack.onAdDismiss(adKey, true)
                controller?.onDismissed()
            }
        }
        interstitialAds.show(activity)
    }

    override fun destroyAd() {
    }


    override fun getAdType(): AdType {
        return AdType.INTERSTITIAL
    }
}