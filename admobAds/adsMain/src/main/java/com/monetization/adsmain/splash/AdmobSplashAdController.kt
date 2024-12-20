package com.monetization.adsmain.splash

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.monetization.appopen.AdmobAppOpenAd
import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.adEnabledSdkString
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs.isRemoteAdEnabled
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.interstitials.AdmobInterstitialAd
import com.monetization.interstitials.AdmobInterstitialAdsManager

class AdmobSplashAdController : DefaultLifecycleObserver {

    private var listener: FullScreenAdsShowListener? = null

    private var splashAdType: SplashAdType = SplashAdType.None
    private var enabled: Boolean = false
    private var mLifecycle: Lifecycle? = null
    private var isScreenInPause: Boolean = false
    private var isAdShowing: Boolean = false
    private var isHandlerRunning: Boolean = false
    private var splashAdLoaded: Boolean = false
    private var splashAdFailed: Boolean = false
    private var bestShowRatesEnabled: Boolean = false
    private val handlerAd = Handler(Looper.getMainLooper())
    private var runnableSplash: Runnable? = null
    private var splashAdTime = 8_000L
    private var splashNormalLoadingTime = 1_000L
    private var activity: Activity? = null

    private var interstitialAd: AdmobInterstitialAd? = null
    private var appOpenAd: AdmobAppOpenAd? = null
    private var showLoadingDialog: (() -> Unit)? = null
    private var loadingListener: AdsLoadingStatusListener? = null

    fun reset() {
        enabled = false
        isScreenInPause = false
        isHandlerRunning = false
        splashAdLoaded = false
        splashAdFailed = false
        bestShowRatesEnabled = false
        isAdShowing = false
        runnableSplash = null
        splashAdTime = 8_000L
        mLifecycle = null
        loadingListener = null
        splashNormalLoadingTime = 1_000L
        activity = null
        try {
            handlerAd.removeCallbacksAndMessages(null)
        } catch (_: Exception) {

        }
    }

    fun showSplashAd(
        enableKey: String,
        adType: SplashAdType,
        activity: Activity,
        timeInMillis: Long,
        callBack: FullScreenAdsShowListener,
        lifecycle: Lifecycle,
        loadingListener: AdsLoadingStatusListener? = null,
        normalLoadingTime: Long = 1_000,
        bestShowRatesEnabled: Boolean = false,
        normalLoadingDialog: (() -> Unit)? = null
    ) {
        val enable = enableKey.isRemoteAdEnabled(adType.getAdKey(), AdType.INTERSTITIAL)
        this.listener = callBack
        this.loadingListener = loadingListener
        this.activity = activity
        this.splashAdType = adType
        this.isAdShowing = false
        this.bestShowRatesEnabled = bestShowRatesEnabled
        this.mLifecycle = lifecycle
        this.splashAdTime = timeInMillis
        this.showLoadingDialog = normalLoadingDialog
        this.splashNormalLoadingTime = normalLoadingTime
        this.enabled = enable
        this.isScreenInPause = false
        this.isHandlerRunning = false
        this.splashAdLoaded = false
        this.splashAdFailed = false
        logAds("Show Splash Called= enable=$enable, adType=$adType")
        if (enable.not() || adType == SplashAdType.None) {
            handlerAd.postDelayed({
                onAdDismissed("Not Enabled $adType")
            }, 2000)
        } else {
            if (mLifecycle?.currentState != Lifecycle.State.DESTROYED) {
                mLifecycle?.addObserver(this)
            }
            startLoadingAds(activity)
        }
    }

    private fun startLoadingAds(activity: Activity) {
        logAds("startLoadingAds Called For Splash")
        startHandler(splashAdTime)
        val loadingStateListener = object : AdsLoadingStatusListener {
            override fun onAdRequested(adKey: String) {
                super.onAdRequested(adKey)
                loadingListener?.onAdRequested(adKey)
            }

            override fun onAdLoaded(adKey: String, mediationClassName: String?) {
                splashAdLoaded = true
                logAds("Splash Ad onAdLoaded ,Handler Running=$isHandlerRunning")
                if (isScreenInPause.not() && listener != null) {
                    removeCallBacks()
                    isAdShowing = true
                    loadingListener?.onAdLoaded(adKey = adKey, mediationClassName)
                    listener?.onAdLoaded(adKey = adKey)
                    if (splashNormalLoadingTime > 0) {
                        showLoadingDialog?.invoke()
                        try {
                            handlerAd.postDelayed({
                                showSplashAd(activity, adKey)
                            }, splashNormalLoadingTime)
                        } catch (_: Exception) {
                            showSplashAd(activity, adKey)
                        }
                    } else {
                        showSplashAd(activity, adKey)
                    }
                }
            }

            override fun onAdFailedToLoad(
                adKey: String,
                message: String,
                code: Int,
                mediationClassName: String?,
                adapterResponses: List<AdsControllerBaseHelper.AdapterResponses>?
            ) {
                logAds(
                    "Splash Ad onAdFailedToLoad,message=$message ,Handler Running=$isHandlerRunning",
                    true
                )
                loadingListener?.onAdFailedToLoad(
                    adKey,
                    message,
                    code,
                    mediationClassName,
                    adapterResponses
                )
                splashAdFailed = true
                if (isScreenInPause.not() && listener != null) {
                    onAdDismissed(adKey)
                }
            }

        }
        loadInterstitialAd(activity, loadingStateListener)
        loadAppOpenAd(activity, loadingStateListener)
    }


    private var isInterShowing = false
    private fun showSplashAd(activity: Activity, adKey: String) {
        listener?.onAdShown(adKey = adKey)
        logAds("showSplashAd Ad Type=$splashAdType")
        val fullScreenAdsShowListener = object : FullScreenAdsShowListener {
            override fun onAdDismiss(adKey: String, adShown: Boolean, rewardEarned: Boolean) {
                listener?.onShowBlackBg(adKey, false)
                if (isInterShowing) {
                    nullAd()
                    onAdDismissed(adKey, adShown)
                }
            }

            override fun onAdShown(adKey: String) {
            }

            override fun onAdShownFailed(adKey: String) {
                nullAd()
                onAdDismiss(adKey)
            }
        }
        when (splashAdType) {
            is SplashAdType.None -> {
                fullScreenAdsShowListener.onAdDismiss("None")
            }

            is SplashAdType.AdmobInter -> {
                isInterShowing = true
                interstitialAd?.showAd(activity, fullScreenAdsShowListener)
            }

            is SplashAdType.AdmobAppOpen -> {
                isInterShowing = true
                listener?.onShowBlackBg((splashAdType as SplashAdType.AdmobAppOpen).key, true)
                appOpenAd?.showAd(activity, fullScreenAdsShowListener)
            }
        }
    }

    private fun nullAd() {
        when (splashAdType) {
            SplashAdType.None -> {

            }

            is SplashAdType.AdmobInter -> {
                interstitialAd = null
            }

            is SplashAdType.AdmobAppOpen -> {
                appOpenAd = null
            }
        }
    }


    private fun loadAppOpenAd(activity: Activity, callback: AdsLoadingStatusListener) {
        if (splashAdType !is SplashAdType.AdmobAppOpen) {
            return
        }
        val adKey = (splashAdType as SplashAdType.AdmobAppOpen).key
        val adController = AdmobAppOpenAdsManager.getAdController(adKey)
        if (adController == null) {
            callback.onAdFailedToLoad(
                adKey,
                "No Controller for app open splash",
                -1,
                mediationClassName = null,
                adapterResponses = null
            )
            return
        }
        val listener = object : AdsLoadingStatusListener {
            override fun onAdRequested(adKey: String) {
                super.onAdRequested(adKey)
                callback.onAdRequested(adKey)
            }

            override fun onAdLoaded(adKey: String, mediationClassName: String?) {
                appOpenAd = adController.getAvailableAd() as? AdmobAppOpenAd
                if (appOpenAd != null) {
                    callback.onAdLoaded(adKey, mediationClassName)
                } else {
                    callback.onAdFailedToLoad(adKey, "onAdLoaded appOpenAd =null", -1, null, null)
                }
            }

            override fun onAdFailedToLoad(
                adKey: String,
                message: String,
                code: Int,
                mediationClassName: String?,
                adapterResponses: List<AdsControllerBaseHelper.AdapterResponses>?
            ) {
                appOpenAd = null
                callback.onAdFailedToLoad(adKey, message, -1, null, null)
            }
        }
        adController.loadAd(
            placementKey = adEnabledSdkString,
            activity = activity,
            calledFrom = "Splash AppOpen",
            callback = listener
        )

    }

    private fun loadInterstitialAd(activity: Activity, callback: AdsLoadingStatusListener) {
        if (splashAdType !is SplashAdType.AdmobInter) {
            return
        }
        val adKey = (splashAdType as SplashAdType.AdmobInter).key
        val adController =
            AdmobInterstitialAdsManager.getAdController(adKey)
        if (adController == null) {
            callback.onAdFailedToLoad(
                adKey,
                "loadInterstitialAd adController == null, key=$adKey",
                -1,
                null,
                null
            )
            return
        }
        val listener = object : AdsLoadingStatusListener {
            override fun onAdRequested(adKey: String) {
                callback.onAdRequested(adKey)
            }

            override fun onAdLoaded(adKey: String, mediationClassName: String?) {
                interstitialAd = adController.getAvailableAd() as? AdmobInterstitialAd
                if (interstitialAd != null) {
                    callback.onAdLoaded(adKey, mediationClassName)
                } else {
                    callback.onAdFailedToLoad(
                        adKey,
                        " onAdLoaded interstitialAd == null",
                        -1,
                        null,
                        null
                    )
                }
            }

            override fun onAdFailedToLoad(
                adKey: String,
                message: String,
                code: Int,
                mediationClassName: String?,
                adapterResponses: List<AdsControllerBaseHelper.AdapterResponses>?
            ) {
                interstitialAd = null
                callback.onAdFailedToLoad(
                    adKey = adKey,
                    message = message,
                    code = code,
                    mediationClassName = mediationClassName,
                    adapterResponses = adapterResponses
                )
            }
        }
        adController.loadAd(
            placementKey = adEnabledSdkString,
            activity = activity,
            calledFrom = "Splash Inter",
            callback = listener
        )
    }

    private fun onAdDismissed(key: String, adShown: Boolean = false) {
        logAds("Splash Ad onAdDismissed Called = $key")
        listener?.onAdDismiss(key, adShown)
        loadingListener = null
        isHandlerRunning = false
        mLifecycle?.removeObserver(this)
        listener = null
        removeCallBacks()
    }


    private fun startHandler(time: Long) {
        if (!isHandlerRunning) {
            isHandlerRunning = true
            runnableSplash = Runnable {
                if (listener != null && isHandlerRunning) {
                    logAds("Splash Time End = $splashAdTime", true)
                    onAdDismissed(splashAdType.toString())
                }
            }
            runnableSplash?.let {
                logAds("Splash Timer started for $splashAdTime", true)
                handlerAd.postDelayed(it, time)
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (isAdShowing) {
            return
        }
        listener?.splashAdOnPause()
        logAds("Splash Ad On Pause", true)
        isScreenInPause = true
        removeCallBacks()
    }


    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        logAds(
            "Splash Ad On Resume isScreenInPause=$isScreenInPause,splashAdFailed=${splashAdFailed},isHandlerRunning=$isHandlerRunning,isInterShowing=${isInterShowing}",
            true
        )
        if (isScreenInPause) {
            listener?.splashAdOnResume()
            isScreenInPause = false
            if (!isHandlerRunning) {
                handlerAd.postDelayed({
                    if (splashAdFailed) {
                        onAdDismissed(splashAdType.toString())
                    } else if (splashAdLoaded && isInterShowing.not()) {
                        activity?.let {
                            showSplashAd(it, "Splash Ad On Resume")
                        }
                    }
                }, 1000)
            }
        }
    }


    private fun removeCallBacks() {
        try {
            runnableSplash?.let {
                isHandlerRunning = false
                handlerAd.removeCallbacks(it)
            }
        } catch (ignored: Exception) {
        }
    }
}