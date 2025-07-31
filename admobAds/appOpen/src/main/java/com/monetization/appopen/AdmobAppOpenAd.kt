package com.monetization.appopen

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.monetization.core.ad_units.GeneralInterOrAppOpenAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.FullScreenAdsShowListener

class AdmobAppOpenAd(
    val appOpenAd: AppOpenAd,
    val adKey: String,
) : GeneralInterOrAppOpenAd {
    override fun showAd(activity: Activity, callBack: FullScreenAdsShowListener) {
        /*if (AdsCommons.isFullScreenAdShowing) {
            return
        }*/
        appOpenAd.setImmersiveMode(true)
        AdsCommons.isFullScreenAdShowing = true
        val controller: AdsControllerBaseHelper? = AdmobAppOpenAdsManager.getAdController(adKey)
        appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                logAds("onAdFailedToShowFullScreenContent Inter", true)
                AdsCommons.isFullScreenAdShowing = false
                controller?.onFailToShow()
                callBack.onAdShownFailed(adKey)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                AdmobAppOpenAdsManager.getAdController(adKey)?.destroyAd(activity)
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
                callBack.onAdDismiss(adKey, true)
            }
        }
        callBack.onAdAboutToShow(adKey)
        appOpenAd.show(activity)
    }

    override fun destroyAd() {
    }

    override fun getAdType(): AdType {
        return AdType.AppOpen
    }

}