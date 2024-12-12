package com.monetization.appopen.extension.counter

import android.app.Activity
import com.monetization.appopen.extension.InstantAppOpenAdsManager
import com.monetization.appopen.extension.PreloadAppOpenAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs.isRemoteAdEnabled
import com.monetization.core.counters.CounterManager
import com.monetization.core.counters.CounterManager.counterWrapper
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

object PreloadCounterAppOpenAdsManager {

    @Deprecated("Please Use FullScreenAdsShowManager to show ads")
    fun tryShowingAppOpenAd(
        placementKey: String,
        key: String,
        counterKey: String?,
        activity: Activity,
        requestNewIfNotAvailable: Boolean = false,
        requestNewIfAdShown: Boolean = false,
        normalLoadingTime: Long = 1_000,
        uiAdsListener: UiAdsListener? = null,
        onCounterUpdate: ((Int) -> Unit)? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        showBlackBg: ((Boolean) -> Unit),
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
    ) {
        val enable = placementKey.isRemoteAdEnabled(key, AdType.AppOpen)
        if (enable.not()) {
            logAds(
                "Ad is not enabled Key=$key,placement=$placementKey,type=${AdType.AppOpen}",
                true
            )
            onAdDismiss.invoke(false, MessagesType.AdNotEnabled)
            return
        }
        counterWrapper(
            counterEnable = !counterKey.isNullOrBlank(),
            key = counterKey,
            onCounterUpdate = onCounterUpdate,
            onDismiss = onAdDismiss
        ) {
            PreloadAppOpenAdsManager.tryShowingAppOpenAd(
                placementKey = placementKey,
                activity = activity,
                key = key,
                normalLoadingTime = normalLoadingTime,
                requestNewIfAdShown = requestNewIfAdShown,
                requestNewIfNotAvailable = requestNewIfNotAvailable,
                uiAdsListener = uiAdsListener,
                onLoadingDialogStatusChange = onLoadingDialogStatusChange,
                onAdDismiss = { adShown, msg ->
                    CounterManager.adShownCounterReact(counterKey, adShown)
                    onAdDismiss.invoke(adShown, msg)
                },
                showBlackBg = showBlackBg
            )
        }
    }
}