package com.example.rewadedad.extensions

import android.app.Activity
import com.example.rewadedad.AdmobRewardedAd
import com.example.rewadedad.AdmobRewardedAdsManager
import com.monetization.core.managers.AdmobBaseInstantAdsManager
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

object InstantRewardedAdsManager : AdmobBaseInstantAdsManager(AdType.REWARDED) {


    fun showInstantRewardedAd(
        placementKey: String,
        activity: Activity,
        key: String,
        normalLoadingTime: Long = 1_000,
        instantLoadingTime: Long = 8_000,
        requestNewIfAdShown: Boolean = false,
        uiAdsListener: UiAdsListener? = null,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onRewarded: (Boolean) -> Unit,
        onAdDismiss: (Boolean, MessagesType?) -> Unit,
    ) {
        val controller = AdmobRewardedAdsManager.getAdController(key)
        canShowAd(
            activity = activity,
            placementKey = placementKey,
            normalLoadingTime = normalLoadingTime,
            instantLoadingTime = instantLoadingTime,
            controller = controller,
            onLoadingDialogStatusChange = onLoadingDialogStatusChange,
            uiAdsListener = uiAdsListener,
            onAdDismiss = onAdDismiss,
            showAd = {
                (controller?.getAvailableAd() as? AdmobRewardedAd)?.showAd(
                    activity = activity,
                    callBack = getShowListener(
                        requestNewIfAdShown = requestNewIfAdShown,
                        activity = activity,
                        controller = controller,
                        adType = AdType.REWARDED,
                        onRewarded = onRewarded
                    )
                )
            }
        )
    }
}





















