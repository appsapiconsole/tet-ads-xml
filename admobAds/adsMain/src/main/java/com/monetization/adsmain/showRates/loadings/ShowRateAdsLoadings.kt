package com.monetization.adsmain.showRates.loadings

import android.app.Activity
import com.monetization.adsmain.commons.loadAd
import com.monetization.adsmain.showRates.inter.showRatesHelper
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.managers.AdsLoadingStatusListener

object ShowRateAdsLoadings {
    /*
        fun String.loadAdWithSR(
            adType: AdType,
            activity: Activity,
            listener: AdsLoadingStatusListener? = null,
            ignoreFromLimit: List<String> = listOf(),
        ) {
            val canRequest = showRatesHelper.canRequestNewAd(adType, ignoreFromLimit)
            if (canRequest) {
                loadAd(adType, activity, listener)
            } else {
                listener?.onAdFailedToLoad(this, "Cannot Load New Ad, Show Rate Limit Reached")
                logAds("Cannot Load New Ad, Show Rate Limit Reached", true)
            }
    }*/
}