package com.monetization.appopen.extension

import android.app.Activity
import com.monetization.appopen.AdmobAppOpenAd
import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.core.managers.AdmobBaseInstantAdsManager
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

object InstantAppOpenAdsManager : AdmobBaseInstantAdsManager(AdType.AppOpen) {


    fun showInstantAppOpenAd(
        placementKey: String,
        activity: Activity,
        key: String,
        normalLoadingTime: Long = 1_000,
        instantLoadingTime: Long = 8_000,
        uiAdsListener: UiAdsListener? = null,
        requestNewIfAdShown: Boolean = false,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        showBlackBg: ((Boolean) -> Unit),
        onAdDismiss: ((Boolean, MessagesType?) -> Unit)? = null,
    ) {
        val controller = AdmobAppOpenAdsManager.getAdController(key)
        canShowAd(
            activity = activity,
            placementKey = placementKey,
            normalLoadingTime = normalLoadingTime,
            instantLoadingTime = instantLoadingTime,
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





















