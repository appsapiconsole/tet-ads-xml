package com.monetization.interstitials

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.provider.ad_request.AdRequestProvider
import com.monetization.core.provider.ad_request.DefaultAdRequestProvider

class AdmobInterstitialAdsController(
    adKey: String,
    adIdsList: List<String>,
    listener: ControllersListener? = null,
    private val adRequestProvider: AdRequestProvider = DefaultAdRequestProvider()
) : AdsControllerBaseHelper(adKey, AdType.INTERSTITIAL, adIdsList, listener) {
    private var currentInterstitialAd: AdmobInterstitialAd? = null

    override fun loadAd(
        placementKey: String,
        activity: Activity,
        calledFrom: String,
        callback: AdsLoadingStatusListener?
    ) {

        val commonLoadChecks = commonLoadAdChecks(placementKey = placementKey, callback = callback)
        if (commonLoadChecks.not()) {
            return
        }
        val adId = getAdIdAndIncrementIndex()

        InterstitialAd.load(activity,
            adId,
            adRequestProvider.getAdRequest(),
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(interAd: InterstitialAd) {
                    super.onAdLoaded(interAd)
                    currentInterstitialAd = null
                    currentInterstitialAd = AdmobInterstitialAd(interAd, getAdKey())
                    currentInterstitialAd?.interstitialAds?.setOnPaidEventListener { paidListener ->
                        val loadedAdapterResponseInfo = interAd.responseInfo?.loadedAdapterResponseInfo
                        val adSourceName: String? = loadedAdapterResponseInfo?.adSourceName
                        val adSourceId: String? = loadedAdapterResponseInfo?.adSourceId
                        val adSourceInstanceName: String? = loadedAdapterResponseInfo?.adSourceInstanceName
                        val adSourceInstanceId: String? = loadedAdapterResponseInfo?.adSourceInstanceId
                        val extras: Bundle? = interAd.responseInfo?.responseExtras
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
                    onLoaded(interAd.responseInfo?.mediationAdapterClassName)

                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    currentInterstitialAd = null
                    onAdFailed(error)
                }
            })
        onAdRequested()
    }

    override fun destroyAd(activity: Activity?) {
        logAds("Inter Ad(${getAdKey()}) Destroyed,Id=${getAdId()}", true)
        currentInterstitialAd = null
    }

    override fun isAdAvailable(): Boolean {
        return currentInterstitialAd != null
    }

    override fun getAvailableAd(): AdUnit? {
        return currentInterstitialAd
    }

}