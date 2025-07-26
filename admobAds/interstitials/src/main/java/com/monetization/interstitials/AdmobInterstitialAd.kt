package com.monetization.interstitials

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.monetization.core.ad_units.GeneralInterOrAppOpenAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.FullScreenAdsShowListener

class AdmobInterstitialAd(
    val interstitialAds: InterstitialAd,
    val adKey: String,
) : GeneralInterOrAppOpenAd {

    override fun showAd(activity: Activity, callBack: FullScreenAdsShowListener) {
        /*if (AdsCommons.isFullScreenAdShowing) {
            return
        }*/
        interstitialAds.setImmersiveMode(true)
        AdsCommons.isFullScreenAdShowing = true
        val controller: AdsControllerBaseHelper? =
            AdmobInterstitialAdsManager.getAdController(adKey)
        interstitialAds.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                logAds("onAdFailedToShowFullScreenContent Inter", true)
                AdsCommons.isFullScreenAdShowing = false
                controller?.onFailToShow()
                callBack.onAdShownFailed(adKey)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                logAds("onAdShowedFullScreenContent Inter")
                AdmobInterstitialAdsManager.getAdController(adKey)?.destroyAd(activity)
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
                logAds("onAdImpression Inter")
                controller?.onImpression()
                callBack.onAdImpression(adKey)
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                logAds("Interstitial onAdDismissedFullScreenContent called", true)
                AdsCommons.isFullScreenAdShowing = false
                controller?.onDismissed()
                callBack.onAdDismiss(adKey, true)
            }
        }
        callBack.onAdAboutToShow(adKey)
        interstitialAds.show(activity)
    }

    override fun destroyAd() {
    }


    override fun getAdType(): AdType {
        return AdType.INTERSTITIAL
    }
}