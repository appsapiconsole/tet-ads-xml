package com.monetization.interstitials.extensions.counter

import android.app.Activity
import com.monetization.core.counters.CounterManager
import com.monetization.core.counters.CounterManager.counterWrapper
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType
import com.monetization.interstitials.extensions.InstantInterstitialAdsManager

object InstantCounterInterAdsManager {

    fun showInstantInterstitialAd(
        placementKey: String,
        activity: Activity,
        key: String,
        counterKey: String?,
        normalLoadingTime: Long = 1_000,
        instantLoadingTime: Long = 8_000,
        requestNewIfAdShown: Boolean = false,
        uiAdsListener: UiAdsListener? = null,
        onCounterUpdate: ((Int) -> Unit)? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
    ) {
        counterWrapper(
            counterEnable = !counterKey.isNullOrBlank(),
            key = counterKey,
            onCounterUpdate = onCounterUpdate,
            onDismiss = onAdDismiss
        ) {
            InstantInterstitialAdsManager.showInstantInterstitialAd(
                placementKey = placementKey,
                activity = activity,
                key = key,
                normalLoadingTime = normalLoadingTime,
                instantLoadingTime = instantLoadingTime,
                requestNewIfAdShown = requestNewIfAdShown,
                uiAdsListener = uiAdsListener,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                onAdDismiss = { adShown, msg ->
                    CounterManager.adShownCounterReact(counterKey, adShown)
                    onAdDismiss.invoke(adShown, msg)
                }
            )
        }
    }
}