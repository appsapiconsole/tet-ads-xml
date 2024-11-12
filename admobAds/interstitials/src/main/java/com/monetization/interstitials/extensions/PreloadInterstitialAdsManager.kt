package com.monetization.interstitials.extensions

import android.app.Activity
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.managers.AdmobBasePreloadAdsManager
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.msgs.MessagesType
import com.monetization.interstitials.AdmobInterstitialAd
import com.monetization.interstitials.AdmobInterstitialAdsManager

object PreloadInterstitialAdsManager : AdmobBasePreloadAdsManager(AdType.INTERSTITIAL) {

    fun tryShowingInterstitialAd(
        placementKey: String,
        key: String,
        activity: Activity,
        requestNewIfNotAvailable: Boolean = false,
        requestNewIfAdShown: Boolean = false,
        normalLoadingTime: Long = 1000,
        uiAdsListener: UiAdsListener? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
    ) {
        val controller = AdmobInterstitialAdsManager.getAdController(key)
        canShowAd(
            activity = activity,
            requestNewIfNotAvailable = requestNewIfNotAvailable,
            placementKey = placementKey,
            normalLoadingTime = normalLoadingTime,
            controller = controller,
            onLoadingDialogStatusChange = onLoadingDialogStatusChange,
            onAdDismiss = onAdDismiss,
            uiAdsListener = uiAdsListener,
            showAd = {
                (controller?.getAvailableAd() as? AdmobInterstitialAd)?.showAd(
                    activity = activity,
                    callBack = getShowListener(
                        requestNewIfAdShown = requestNewIfAdShown,
                        activity = activity,
                        controller = controller,
                        adType = AdType.INTERSTITIAL
                    )
                )
            }
        )
    }

}