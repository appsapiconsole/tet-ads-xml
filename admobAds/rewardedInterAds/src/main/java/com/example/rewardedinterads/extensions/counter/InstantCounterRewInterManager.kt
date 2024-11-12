package com.example.rewardedinterads.extensions.counter

import android.app.Activity
import com.example.rewardedinterads.extensions.InstantRewardedInterAdsManager
import com.monetization.core.counters.CounterManager
import com.monetization.core.counters.CounterManager.counterWrapper
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

object InstantCounterRewInterManager {


    fun showInstantRewardedInterstitialAd(
        enableKey: String,
        activity: Activity,
        key: String,
        counterKey: String?,
        normalLoadingTime: Long = 1_000,
        instantLoadingTime: Long = 8_000,
        requestNewIfAdShown: Boolean = false,
        uiAdsListener: UiAdsListener? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onCounterUpdate: ((Int) -> Unit)? = null,
        onRewarded: (Boolean) -> Unit,
        onAdDismiss: (Boolean,MessagesType?) -> Unit,
    ) {
        counterWrapper(
            counterEnable = !counterKey.isNullOrBlank(),
            key = counterKey,
            onDismiss = onAdDismiss,
            onCounterUpdate = onCounterUpdate
        ) {
            InstantRewardedInterAdsManager.showInstantRewardedInterstitialAd(enableKey = enableKey,
                activity = activity,
                key = key,
                normalLoadingTime = normalLoadingTime,
                instantLoadingTime = instantLoadingTime,
                requestNewIfAdShown = requestNewIfAdShown,
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





















