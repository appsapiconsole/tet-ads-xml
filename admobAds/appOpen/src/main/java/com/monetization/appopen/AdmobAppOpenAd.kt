package com.monetization.appopen

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ad_units.GeneralInterOrAppOpenAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons

class AdmobAppOpenAd(
    val appOpenAd: AppOpenAd,
    val adKey: String,
) : GeneralInterOrAppOpenAd {
    override fun showAd(activity: Activity, callBack: FullScreenAdsShowListener) {
        if (AdsCommons.isFullScreenAdShowing) {
            return
        }
        AdsCommons.isFullScreenAdShowing = true
        val controller: AdsControllerBaseHelper? = AdmobAppOpenAdsManager.getAdController(adKey)
        appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                AdsCommons.isFullScreenAdShowing = false
                AdmobAppOpenAdsManager.getAdController(adKey)?.destroyAd(activity)
                callBack.onAdDismiss(adKey)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                AdmobAppOpenAdsManager.getAdController(adKey)?.destroyAd(activity)
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
                callBack.onAdDismiss(adKey, true)
                controller?.onDismissed()
            }
        }
        appOpenAd.show(activity)
    }

    override fun destroyAd() {
    }

    override fun getAdType(): AdType {
        return AdType.AppOpen
    }

}