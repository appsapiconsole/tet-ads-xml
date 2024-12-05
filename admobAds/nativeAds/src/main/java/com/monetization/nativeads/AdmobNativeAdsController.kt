package com.monetization.nativeads

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.nativead.NativeAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.provider.ad_request.AdRequestProvider
import com.monetization.core.provider.ad_request.DefaultAdRequestProvider
import com.monetization.core.provider.nativeAds.DefaultNativeOptionsProvider
import com.monetization.core.provider.nativeAds.NativeOptionsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdmobNativeAdsController(
    adKey: String, adIdsList: List<String>,
    listener: ControllersListener? = null,
    private val adRequestProvider: AdRequestProvider = DefaultAdRequestProvider(),
    private val nativeAdOptions: NativeOptionsProvider = DefaultNativeOptionsProvider(),
) : AdsControllerBaseHelper(adKey, AdType.NATIVE, adIdsList, listener) {

    private var currentNativeAd: AdmobNativeAd? = null
    private var nativeAdOptionsProvider: NativeOptionsProvider = nativeAdOptions

    private var shownNativeAds = mutableListOf<AdmobNativeAd>()

    fun adShown(admobNativeAd: AdmobNativeAd) {
        shownNativeAds.add(admobNativeAd)
    }

    fun setNativeAdOptions(provider: NativeOptionsProvider) {
        nativeAdOptionsProvider = provider
    }

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
                val loadedAdapterResponseInfo = nativeAd.responseInfo?.loadedAdapterResponseInfo
                val adSourceName: String? = loadedAdapterResponseInfo?.adSourceName
                val adSourceId: String? = loadedAdapterResponseInfo?.adSourceId
                val adSourceInstanceName: String? = loadedAdapterResponseInfo?.adSourceInstanceName
                val adSourceInstanceId: String? = loadedAdapterResponseInfo?.adSourceInstanceId
                val extras: Bundle? = nativeAd.responseInfo?.responseExtras
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
            CoroutineScope(Dispatchers.Main).launch {

                onLoaded(mediationClassName = nativeAd.responseInfo?.mediationAdapterClassName)
            }

        }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    currentNativeAd = null
                    onAdFailed(error)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    onImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    onAdClick()
                }
            }).withNativeAdOptions(
                nativeAdOptionsProvider.getNativeAdOptions()
            ).build()
        adLoader.loadAd(adRequestProvider.getAdRequest())
        onAdRequested()
    }

    override fun destroyAd(activity: Activity?) {
        logAds("Native Ad(${getAdKey()}) Destroyed,Id=${getAdId()}", true)
        currentNativeAd?.let {
            adShown(it)
        }
        currentNativeAd = null
    }

    override fun isAdAvailable(): Boolean {
        return currentNativeAd != null
    }

    override fun getAvailableAd(): AdUnit? {
        return currentNativeAd
    }


}