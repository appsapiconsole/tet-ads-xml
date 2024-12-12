package com.monetization.core.managers

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.commons.AdsCommons.isFullScreenAdShowing
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.commons.SdkConfigs.isRemoteAdEnabled
import com.monetization.core.controllers.AdsController
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

abstract class AdmobBasePreloadAdsManager(
    private val adType: AdType,
) {
    private var loadingDialogListener: ((Boolean) -> Unit)? = null

    private var uiAdsListener: UiAdsListener? = null

    private var onDismissListener: ((Boolean, MessagesType?) -> Unit)? = null

    private fun onFreeAd(msgType: MessagesType?, check: Boolean = false) {
        isFullScreenAdShowing = false
        uiAdsListener = null
        onDismissListener?.invoke(check, msgType)
        onDismissListener = null
    }

    private fun allowed(key: String, placementKey: String): Boolean {
        val allowed = SdkConfigs.canShowAds(
            adKey = key,
            placementKey = placementKey,
            adType = adType
        )
        if (allowed.not()) {
            logAds("Ad is restricted by Sdk to show Key=$key,type=$adType", true)
        }
        return allowed
    }

    fun setUiListener(listener: UiAdsListener) {
        uiAdsListener = listener
    }

    fun canShowAd(
        activity: Activity,
        placementKey: String,
        normalLoadingTime: Long = 1_000,
        controller: AdsController?,
        requestNewIfNotAvailable: Boolean,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        uiAdsListener: UiAdsListener?,
        onAdDismiss: ((Boolean, MessagesType?) -> Unit)? = null,
        showAd: () -> Unit,
    ) {
        val key = controller?.getAdKey() ?: ""

        if (isFullScreenAdShowing) {
            logAds("Full Screen Ad is already showing", true)
            return
        }
        loadingDialogListener = onLoadingDialogStatusChange
        onDismissListener = onAdDismiss
        this.uiAdsListener = uiAdsListener
        isFullScreenAdShowing = true
        val availableAd = controller?.getAvailableAd()
        if (controller == null) {
            logAds("No Controller Found Against $key,$adType", true)
            onFreeAd(MessagesType.NoController)
            return
        }
        val enabled =
            placementKey.isRemoteAdEnabled(key, controller.getAdType() ?: AdType.INTERSTITIAL)
        if (enabled.not()) {
            logAds("$adType:$key,is not enabled", true)
            onFreeAd(MessagesType.AdNotEnabled)
            return
        }
        if (requestNewIfNotAvailable && availableAd == null) {
            logAds("$adType:$key,New Ad Request as no ad is available to show", true)
            controller.loadAd(
                placementKey = placementKey,
                activity = activity,
                calledFrom = "",
                callback = null
            )
            onFreeAd(MessagesType.AdNotAvailable)
            return
        }
        if (availableAd == null) {
            logAds("$adType:$key, No Ad To Show, Please Request an ad First", true)
            onFreeAd(MessagesType.AdNotAvailable)
            return
        }
        if (allowed(key = key, placementKey = placementKey).not()) {
            onFreeAd(MessagesType.ShowingNotAllowed)
            return
        }
        if (normalLoadingTime > 0) {
            loadingDialogListener?.invoke(true)
        }
        nowShowAd(
            normalLoadingTime = normalLoadingTime,
            controller = controller,
            showAd = showAd
        )
    }

    private fun nowShowAd(
        normalLoadingTime: Long,
        controller: AdsController,
        showAd: () -> Unit,
    ) {
        val adToShow = controller.getAvailableAd()
        if (adToShow != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                loadingDialogListener?.invoke(false)
                showAd.invoke()
            }, normalLoadingTime)
        } else {
            logAds("$adType:No Ad is available to show", true)
            loadingDialogListener?.invoke(false)
            onFreeAd(MessagesType.AdNotAvailable)
        }
    }

    fun getShowListener(
        requestNewIfAdShown: Boolean,
        activity: Activity,
        controller: AdsController,
        adType: AdType,
        showBlackBg: ((Boolean) -> Unit)? = null,
        onRewarded: ((Boolean) -> Unit)? = null,
    ): FullScreenAdsShowListener {
        return AdsCommons.getShowListener(
            requestNewIfAdShown = requestNewIfAdShown,
            activity = activity,
            controller = controller,
            adType = adType,
            showBlackBg = showBlackBg,
            onRewarded = onRewarded,
            uiAdsListener = uiAdsListener,
            onFreeAd = { msg, adShown ->
                onFreeAd(null, adShown)
            }
        )
    }


}