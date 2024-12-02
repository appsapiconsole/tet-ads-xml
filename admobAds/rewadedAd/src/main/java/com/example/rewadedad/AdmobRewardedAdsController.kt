package com.example.rewadedad

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener

class AdmobRewardedAdsController(
    adKey: String, adIdsList: List<String>,
    listener: ControllersListener? = null,
) : AdsControllerBaseHelper(adKey, AdType.REWARDED, adIdsList, listener) {

    private var currentRewardedAd: AdmobRewardedAd? = null

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
        RewardedAd.load(activity,
            adId, adRequest, object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    super.onAdLoaded(ad)
                    currentRewardedAd = null
                    currentRewardedAd = AdmobRewardedAd(ad, getAdKey())
                    currentRewardedAd?.rewardedAd?.setOnPaidEventListener { paidListener ->
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
                    currentRewardedAd = null
                    onAdFailed(error.message, error.code)
                }
            }
        )
        onAdRequested()
    }

    override fun destroyAd(activity: Activity?) {
        currentRewardedAd = null
    }

    override fun isAdAvailable(): Boolean {
        return currentRewardedAd != null
    }

    override fun getAvailableAd(): AdUnit? {
        return currentRewardedAd
    }


}