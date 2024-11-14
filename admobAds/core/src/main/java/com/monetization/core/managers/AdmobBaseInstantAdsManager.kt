package com.monetization.core.managers

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.commons.AdsCommons.adEnabledSdkString
import com.monetization.core.commons.AdsCommons.isFullScreenAdShowing
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.commons.SdkConfigs.isRemoteAdEnabled
import com.monetization.core.controllers.AdsController
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType

abstract class AdmobBaseInstantAdsManager(private val adType: AdType) {

    private var adHandler: Handler? = null

    private var loadingDialogListener: ((Boolean) -> Unit)? = null

    private var uiAdsListener: UiAdsListener? = null

    var onDismissListener: ((Boolean, MessagesType?) -> Unit)? = null

    private var isHandlerRunning = false

    private val runnable = Runnable {
        if (isHandlerRunning && onDismissListener != null && isFullScreenAdShowing.not()) {
            loadingDialogListener?.invoke(false)
            onFreeAd(MessagesType.InstantTimeEnd)
            onDismissListener = null
            stopHandler()
        }
    }

    fun onFreeAd(messagesType: MessagesType?, check: Boolean = false) {
        onDismissListener?.invoke(check, messagesType)
        uiAdsListener = null
        onDismissListener = null
        isFullScreenAdShowing = false
        stopHandler()
    }


    private fun startHandler(time: Long) {
        stopHandler()
        isHandlerRunning = true
        adHandler = Handler(Looper.getMainLooper())
        adHandler?.postDelayed(runnable, time)
    }

    fun stopHandler() {
        if (isHandlerRunning) {
            try {
                isHandlerRunning = false
                adHandler?.removeCallbacks(runnable)
                adHandler?.removeCallbacksAndMessages(null)
                adHandler = null
            } catch (_: Exception) {
            }
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
            onFreeAd = { msg, adShown ->
                onFreeAd(null, adShown)
            },
            uiAdsListener = uiAdsListener
        )
    }

    fun setUiListener(listener: UiAdsListener) {
        uiAdsListener = listener
    }

    fun canShowAd(
        activity: Activity,
        placementKey: String,
        normalLoadingTime: Long = 1_000,
        instantLoadingTime: Long = 8_000,
        controller: AdsController?,
        uiAdsListener: UiAdsListener?,
        onLoadingDialogStatusChange: (Boolean) -> Unit,
        onAdDismiss: ((Boolean, MessagesType?) -> Unit)? = null,
        showAd: () -> Unit,
    ) {
        val key = controller?.getAdKey() ?: ""
        if (AdsCommons.isFullScreenAdShowing) {
            logAds("Full Screen Ad is already showing")
            return
        }
        loadingDialogListener = onLoadingDialogStatusChange
        onDismissListener = onAdDismiss
        this.uiAdsListener = uiAdsListener
        if (controller == null) {
            logAds("No Controller Available Key=$key,type=$adType", true)
            onFreeAd(MessagesType.NoController)
            return
        }
        val enable = placementKey.isRemoteAdEnabled(key, controller.getAdType())
        if (enable.not()) {
            logAds("Ad is not enabled Key=$key,placement=$placementKey,type=$adType", true)
            onFreeAd(MessagesType.AdNotEnabled)
            return
        }
        if (SdkConfigs.canShowAds(adKey = key, placementKey = placementKey, adType = adType).not()) {
            logAds("Ad is restricted by Sdk to show Key=$key,type=$adType", true)
            onFreeAd(MessagesType.ShowingNotAllowed)
            return
        }
        isHandlerRunning = false
        if (normalLoadingTime > 0 || instantLoadingTime > 0) {
            loadingDialogListener?.invoke(true)
        }
        nowShowAd(
            activity = activity,
            normalLoadingTime = normalLoadingTime,
            instantLoadingTime = instantLoadingTime,
            controller = controller,
            showAd = showAd
        )
    }

    private fun nowShowAd(
        activity: Activity,
        normalLoadingTime: Long,
        instantLoadingTime: Long,
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
            startHandler(instantLoadingTime)
            uiAdsListener?.onAdRequested(key = controller.getAdKey())
            controller.loadAd(
                placementKey = adEnabledSdkString,
                activity = activity,
                calledFrom = "showInstantAd ad to show null",
                callback = object : AdsLoadingStatusListener {
                    override fun onAdLoaded(adKey: String) {
                        loadingDialogListener?.invoke(false)
                        uiAdsListener?.onAdLoaded(adKey)
                        val newAd = controller.getAvailableAd()
                        logAds("showInstantAd onAdLoaded, Checks = ${onDismissListener != null && newAd != null && activity.isFinishing.not() && activity.isDestroyed.not()}")
                        if (onDismissListener != null && newAd != null && activity.isFinishing.not() && activity.isDestroyed.not()) {
                            showAd.invoke()
                        } else {
                            stopHandler()
                            onFreeAd(MessagesType.AdLoadedButNoListeners)
                        }
                    }

                    override fun onAdFailedToLoad(adKey: String, message: String, code: Int) {
                        AdsCommons.logAds("showInstantAd onAdFailedToLoad $message,$code")
                        loadingDialogListener?.invoke(false)
                        uiAdsListener?.onAdFailed(adKey, message, code)
                        stopHandler()
                        onFreeAd(MessagesType.AdLoadFailed)
                    }
                })
        }
    }


}