package com.example.rewardedinterads.extensions.counter

import android.app.Activity
import com.example.rewardedinterads.extensions.PreloadRewardedInterAdsManager
import com.monetization.core.counters.CounterManager
import com.monetization.core.counters.CounterManager.counterWrapper
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

object PreloadCounterRewInterManager {

    fun tryShowingRewardedInterAd(
        placementKey: String,
        key: String,
        counterKey: String?,
        activity: Activity,
        requestNewIfNotAvailable: Boolean = true,
        requestNewIfAdShown: Boolean = true,
        normalLoadingTime: Long = 1000,
        uiAdsListener: UiAdsListener? = null,
        onCounterUpdate: ((Int) -> Unit)? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onRewarded: (Boolean) -> Unit,
        onAdDismiss: (Boolean,MessagesType?) -> Unit,
    ) {
        counterWrapper(
            counterEnable = !counterKey.isNullOrBlank(),
            key = counterKey,
            onDismiss = onAdDismiss,
            onCounterUpdate = onCounterUpdate
        ) {
            PreloadRewardedInterAdsManager.tryShowingRewardedInterAd(
                placementKey = placementKey,
                key = key,
                activity = activity,
                requestNewIfNotAvailable = requestNewIfNotAvailable,
                requestNewIfAdShown = requestNewIfAdShown,
                normalLoadingTime = normalLoadingTime,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                onRewarded = onRewarded,
                uiAdsListener = uiAdsListener,
                onAdDismiss = { adShown,msg ->
                    CounterManager.adShownCounterReact(counterKey, adShown)
                    onAdDismiss.invoke(adShown,msg)
                }
            )
        }
    }
}