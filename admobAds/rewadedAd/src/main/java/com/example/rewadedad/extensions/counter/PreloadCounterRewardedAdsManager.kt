package com.example.rewadedad.extensions.counter

import android.app.Activity
import com.example.rewadedad.extensions.PreloadRewardedAdsManager
import com.monetization.core.counters.CounterManager
import com.monetization.core.counters.CounterManager.counterWrapper
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

object PreloadCounterRewardedAdsManager {

    fun tryShowingRewardedAd(
        placementKey: String,
        key: String,
        counterKey: String?,
        activity: Activity,
        requestNewIfNotAvailable: Boolean = true,
        requestNewIfAdShown: Boolean = true,
        uiAdsListener: UiAdsListener? = null,
        normalLoadingTime: Long = 1000,
        onCounterUpdate: ((Int) -> Unit)? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onRewarded: (Boolean) -> Unit,
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
    ) {
        counterWrapper(
            counterEnable = !counterKey.isNullOrBlank(),
            key = counterKey,
            onDismiss = onAdDismiss,
            onCounterUpdate = onCounterUpdate
        ) {
            PreloadRewardedAdsManager.tryShowingRewardedAd(
                placementKey = placementKey,
                key = key,
                activity = activity,
                requestNewIfNotAvailable = requestNewIfNotAvailable,
                requestNewIfAdShown = requestNewIfAdShown,
                normalLoadingTime = normalLoadingTime,
                uiAdsListener = uiAdsListener,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                onRewarded = onRewarded,
                onAdDismiss = { adShown, msg ->
                    CounterManager.adShownCounterReact(counterKey, adShown)
                    onAdDismiss.invoke(adShown, msg)
                }
            )
        }
    }
}