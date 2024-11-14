package com.monetization.interstitials.extensions.counter

import android.app.Activity
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs.isRemoteAdEnabled
import com.monetization.core.counters.CounterManager
import com.monetization.core.counters.CounterManager.counterWrapper
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType
import com.monetization.interstitials.extensions.PreloadInterstitialAdsManager

object PreloadCounterInterAdsManager {

    fun tryShowingInterstitialAd(
        placementKey: String,
        key: String,
        counterKey: String?,
        activity: Activity,
        requestNewIfNotAvailable: Boolean = false,
        requestNewIfAdShown: Boolean = false,
        normalLoadingTime: Long = 1000,
        uiAdsListener: UiAdsListener? = null,
        onCounterUpdate: ((Int) -> Unit)? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
    ) {

        val enable = placementKey.isRemoteAdEnabled(key, AdType.INTERSTITIAL)
        if (enable.not()) {
            logAds(
                "Ad is not enabled Key=$key,placement=$placementKey,type=${AdType.INTERSTITIAL}",
                true
            )
            onAdDismiss.invoke(false, MessagesType.AdNotEnabled)
            return
        }
        counterWrapper(
            counterEnable = !counterKey.isNullOrBlank(),
            key = counterKey,
            onDismiss = onAdDismiss,
            onCounterUpdate = onCounterUpdate
        ) {
            PreloadInterstitialAdsManager.tryShowingInterstitialAd(
                placementKey = placementKey,
                key = key,
                activity = activity,
                requestNewIfNotAvailable = requestNewIfNotAvailable,
                requestNewIfAdShown = requestNewIfAdShown,
                normalLoadingTime = normalLoadingTime,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                uiAdsListener = uiAdsListener,
                onAdDismiss = { adShown, msg ->
                    CounterManager.adShownCounterReact(counterKey, adShown)
                    onAdDismiss.invoke(adShown, msg)
                }
            )
        }
    }

}