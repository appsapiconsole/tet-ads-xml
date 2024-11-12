package com.monetization.nativeads

import android.app.Activity
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdmobNativeAdsController(
    adKey: String, adIdsList: List<String>,
    listener: ControllersListener? = null,
) : AdsControllerBaseHelper(adKey, AdType.NATIVE, adIdsList, listener) {

    private var currentNativeAd: AdmobNativeAd? = null

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
        val adLoader = AdLoader.Builder(activity, adId).forNativeAd { nativeAd: NativeAd ->
            currentNativeAd?.destroyAd(activity)
            currentNativeAd = AdmobNativeAd(getAdKey(), nativeAd)
            currentNativeAd?.nativeAd?.setOnPaidEventListener { paidListener ->
                onAdRevenue(
                    value = paidListener.valueMicros,
                    currencyCode = paidListener.currencyCode,
                    precisionType = paidListener.precisionType
                )
            }
            CoroutineScope(Dispatchers.Main).launch {
                onLoaded()
            }

        }.withAdListener(object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                currentNativeAd = null
                onAdFailed(error.message, error.code)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                onImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                onAdClick()
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
        adLoader.loadAd(AdRequest.Builder().build())
        onAdRequested()
    }

    override fun destroyAd(activity: Activity?) {
        logAds("Native Ad(${getAdKey()}) Destroyed,Id=${getAdId()}", true)
        currentNativeAd = null
    }

    override fun isAdAvailable(): Boolean {
        return currentNativeAd != null
    }

    override fun getAvailableAd(): AdUnit? {
        return currentNativeAd
    }


}