package com.monetization.appopen.extension

import android.app.Activity
import com.monetization.appopen.AdmobAppOpenAd
import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.core.managers.AdmobBasePreloadAdsManager
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

object PreloadAppOpenAdsManager : AdmobBasePreloadAdsManager(AdType.AppOpen) {

    fun tryShowingAppOpenAd(
        placementKey: String,
        key: String,
        activity: Activity,
        requestNewIfNotAvailable: Boolean = true,
        requestNewIfAdShown: Boolean = true,
        uiAdsListener: UiAdsListener? = null,
        normalLoadingTime: Long = 1000,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        showBlackBg: (Boolean) -> Unit,
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
    ) {

        val controller = AdmobAppOpenAdsManager.getAdController(key)
        canShowAd(
            activity = activity,
            requestNewIfNotAvailable = requestNewIfNotAvailable,
            placementKey = placementKey,
            normalLoadingTime = normalLoadingTime,
            controller = controller,
            uiAdsListener = uiAdsListener,
            onLoadingDialogStatusChange = onLoadingDialogStatusChange,
            onAdDismiss = onAdDismiss,
            showAd = {
                showBlackBg.invoke(true)
                (controller?.getAvailableAd() as? AdmobAppOpenAd)?.showAd(
                    activity = activity,
                    callBack = getShowListener(
                        requestNewIfAdShown = requestNewIfAdShown,
                        activity = activity,
                        controller = controller,
                        adType = AdType.AppOpen,
                        showBlackBg = showBlackBg
                    )
                )
            }
        )
    }
}