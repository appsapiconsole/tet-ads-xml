package com.monetization.appopen

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener

class AdmobAppOpenAdsController(
    adKey: String,
    adIdsList: List<String>,
    listener: ControllersListener? = null
) : AdsControllerBaseHelper(adKey, AdType.AppOpen, adIdsList, listener) {
    private var currentAppOpenAd: AdmobAppOpenAd? = null

    override fun loadAd(
        placementKey: String,
        activity: Activity,
        calledFrom: String,
        callback: AdsLoadingStatusListener?
    ) {
        val commonLoadChecks = commonLoadAdChecks(placementKey = placementKey, callback)
        if (commonLoadChecks.not()) {
            return
        }
        val adId = getAdIdAndIncrementIndex()
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            activity, adId, request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    super.onAdLoaded(ad)
                    currentAppOpenAd?.destroyAd()
                    currentAppOpenAd = AdmobAppOpenAd(ad, getAdKey())
                    currentAppOpenAd?.appOpenAd?.setOnPaidEventListener { paidListener ->
                        onAdRevenue(
                            value = paidListener.valueMicros,
                            currencyCode = paidListener.currencyCode,
                            precisionType = paidListener.precisionType
                        )
                    }
                    onLoaded(ad.responseInfo.mediationAdapterClassName)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    currentAppOpenAd = null
                    onAdFailed(error.message, error.code)
                }
            }
        )
        onAdRequested()
    }

    override fun destroyAd(activity: Activity?) {
        currentAppOpenAd = null
    }

    override fun isAdAvailable(): Boolean {
        return currentAppOpenAd != null
    }

    override fun getAvailableAd(): AdUnit? {
        return currentAppOpenAd
    }
}