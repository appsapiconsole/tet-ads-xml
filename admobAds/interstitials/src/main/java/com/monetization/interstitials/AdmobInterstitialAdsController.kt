package com.monetization.interstitials

import android.app.Activity
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

class AdmobInterstitialAdsController(
    adKey: String,
    adIdsList: List<String>,
    listener: ControllersListener? = null
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
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(interAd: InterstitialAd) {
                    super.onAdLoaded(interAd)
                    currentInterstitialAd = null
                    currentInterstitialAd = AdmobInterstitialAd(interAd, getAdKey())
                    currentInterstitialAd?.interstitialAds?.setOnPaidEventListener { paidListener ->
                        onAdRevenue(
                            value = paidListener.valueMicros,
                            currencyCode = paidListener.currencyCode,
                            precisionType = paidListener.precisionType
                        )
                    }
                    onLoaded(interAd.responseInfo?.mediationAdapterClassName)

                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    currentInterstitialAd = null
                    onAdFailed(error.message, error.code)
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