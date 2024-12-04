package com.monetization.appopen

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.provider.ad_request.AdRequestProvider
import com.monetization.core.provider.ad_request.DefaultAdRequestProvider

class AdmobAppOpenAdsController(
    adKey: String,
    adIdsList: List<String>,
    listener: ControllersListener? = null,
    private val adRequestProvider: AdRequestProvider = DefaultAdRequestProvider()
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
        val request = adRequestProvider.getAdRequest()
        AppOpenAd.load(
            activity, adId, request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    super.onAdLoaded(ad)
                    currentAppOpenAd?.destroyAd()
                    currentAppOpenAd = AdmobAppOpenAd(ad, getAdKey())
                    currentAppOpenAd?.appOpenAd?.setOnPaidEventListener { paidListener ->
                        val loadedAdapterResponseInfo = ad.responseInfo.loadedAdapterResponseInfo
                        val adSourceName: String? = loadedAdapterResponseInfo?.adSourceName
                        val adSourceId: String? = loadedAdapterResponseInfo?.adSourceId
                        val adSourceInstanceName: String? =
                            loadedAdapterResponseInfo?.adSourceInstanceName
                        val adSourceInstanceId: String? =
                            loadedAdapterResponseInfo?.adSourceInstanceId
                        val extras: Bundle = ad.responseInfo.responseExtras

                        onAdRevenue(
                            value = paidListener.valueMicros,
                            currencyCode = paidListener.currencyCode,
                            precisionType = paidListener.precisionType,
                            adSourceName = adSourceName,
                            adSourceId = adSourceId,
                            adSourceInstanceName = adSourceInstanceName,
                            adSourceInstanceId = adSourceInstanceId,
                            extras = extras
                        )
                    }
                    onLoaded(ad.responseInfo.mediationAdapterClassName)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    currentAppOpenAd = null
                    onAdFailed(error)
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