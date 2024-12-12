package com.example.rewardedinterads

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.controllers.ControllerState
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.provider.ad_request.AdRequestProvider
import com.monetization.core.provider.ad_request.DefaultAdRequestProvider

class AdmobRewardedInterAdsController(
    adKey: String, adIdsList: List<String>,
    listener: ControllersListener? = null,
    private val adRequestProvider: AdRequestProvider = DefaultAdRequestProvider()
) : AdsControllerBaseHelper(adKey, AdType.REWARDED_INTERSTITIAL, adIdsList, listener) {
    private var rewardedInterAd: AdmobRewardedInterAd? = null


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
        val adRequest = adRequestProvider.getAdRequest()
        RewardedInterstitialAd.load(activity,
            adId, adRequest, object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    super.onAdLoaded(ad)
                    // If your app is going to request only one native ad at a time, set the currentNativeAd reference to null.
                    rewardedInterAd = null
                    rewardedInterAd = AdmobRewardedInterAd(ad, getAdKey())
                    rewardedInterAd?.rewardedInter?.setOnPaidEventListener { paidListener ->
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
                    onLoaded(ad.responseInfo?.mediationAdapterClassName)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    rewardedInterAd = null
                    onAdFailed(error)
                }
            })
        onAdRequested()
    }


    override fun destroyAd(activity: Activity?) {
        rewardedInterAd = null
        setControllerState(ControllerState.NoAdAvailable)
    }


    override fun getAvailableAd(): AdUnit? {
        return rewardedInterAd
    }


}