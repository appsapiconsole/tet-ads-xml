package com.example.rewardedinterads

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.listeners.ControllersListener

class AdmobRewardedInterAdsController(
    adKey: String, adIdsList: List<String>,
    listener: ControllersListener? = null,
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
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(activity,
            adId, adRequest, object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    super.onAdLoaded(ad)
                    // If your app is going to request only one native ad at a time, set the currentNativeAd reference to null.
                    rewardedInterAd = null
                    rewardedInterAd = AdmobRewardedInterAd(ad, getAdKey())
                    rewardedInterAd?.rewardedInter?.setOnPaidEventListener { paidListener ->
                        onAdRevenue(
                            value = paidListener.valueMicros,
                            currencyCode = paidListener.currencyCode,
                            precisionType = paidListener.precisionType
                        )
                    }
                    onLoaded(ad.responseInfo?.mediationAdapterClassName)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    rewardedInterAd = null
                    onAdFailed(error.message, error.code)
                }
            })
        onAdRequested()
    }


    override fun destroyAd(activity: Activity?) {
        rewardedInterAd = null
    }

    override fun isAdAvailable(): Boolean {
        return rewardedInterAd != null
    }

    override fun getAvailableAd(): AdUnit? {
        return rewardedInterAd
    }


}