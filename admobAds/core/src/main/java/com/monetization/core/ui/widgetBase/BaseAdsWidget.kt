package com.monetization.core.ui.widgetBase

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.commons.AdsCommons.getGoodName
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.NativeConstants.inflateLayoutByLayoutInfo
import com.monetization.core.commons.NativeConstants.makeGone
import com.monetization.core.commons.NativeConstants.makeVisible
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.controllers.AdsController
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.managers.AdsManager
import com.monetization.core.models.RefreshAdInfo
import com.monetization.core.ui.AdsWidgetData
import com.monetization.core.ui.ShimmerInfo

abstract class BaseAdsWidget<T : AdsControllerBaseHelper> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), DefaultLifecycleObserver {

    var adUnit: AdUnit? = null
    var lifecycle: Lifecycle? = null
    var adsController: AdsController? = null
    var isShowAdCalled = false
    var adPopulated = false
    var isLoadAdCalled = false
    var activity: Activity? = null
    var key = ""
    var placementKey = ""
    var isAdEnabled = false
    private var isViewInPause = false
    var adLoaded = false
    var oneTimeUse = false
    var requestNewOnShow = true
    var isValuesFromRemote = false
    var showFromHistory = false
    private var isForRefresh = false
    var isAdFailedToLoad = false
    private var refreshAdInfo: RefreshAdInfo = RefreshAdInfo()
    var shimmerInfo: ShimmerInfo = ShimmerInfo.GivenLayout()
    var uiListener: UiAdsListener? = null

    var adsWidgetData: AdsWidgetData? = null
    private var adsManager: AdsManager<T>? = null

    fun setAdsWidgetData(adsWidgetData: AdsWidgetData?, isValuesFromRemote: Boolean) {
        logAds("setAdsWidgetData(fromRemote=$isValuesFromRemote) Widget Data=$adsWidgetData")
        this.isValuesFromRemote = isValuesFromRemote
        this.adsWidgetData = adsWidgetData
    }

    fun attachWithLifecycle(lifecycle: Lifecycle, isJetpackCompose: Boolean) {
        this.lifecycle = lifecycle
        if (isJetpackCompose.not()) {
            attachLifecycle(lifecycle)
        }
    }

    fun attachLifecycle(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        try {
            lifecycle.addObserver(this)
        } catch (_: Exception) {
        }
    }

    fun removeLifecycle() {
        try {
            lifecycle?.removeObserver(this)
        } catch (_: Exception) {
        }
    }

    fun onShowAdCalled(
        adKey: String,
        activity: Activity,
        oneTimeUse: Boolean,
        requestNewOnShow: Boolean,
        enabled: Boolean,
        shimmerInfo: ShimmerInfo,
        adsManager: AdsManager<T>,
        adType: AdType,
        listener: UiAdsListener?,
        isForRefresh: Boolean = false,
        showFromHistory: Boolean = false,
        refreshAdInfo: RefreshAdInfo = RefreshAdInfo()
    ) {
        if (SdkConfigs.canShowAds(adKey, placementKey, adType).not()) {
            logAds("Ad Showing is restricted against key=$adKey for $adType", true)
            makeGone()
            return
        }
        makeVisible()
        if (isForRefresh.not()) {
            this.uiListener = listener
        }
        this.key = adKey
        this.showFromHistory = showFromHistory
        this.activity = activity
        this.oneTimeUse = oneTimeUse
        this.refreshAdInfo = refreshAdInfo
        this.isForRefresh = isForRefresh
        this.requestNewOnShow = requestNewOnShow
        this.isAdEnabled = enabled
        this.shimmerInfo = shimmerInfo
        this.adsManager = adsManager
        this.isAdFailedToLoad = false
        this.adLoaded = false
        this.isShowAdCalled = true
        this.adPopulated = false
        loadAdCalled(adsManager)
    }

    fun getAdsLoadingListener(): AdsLoadingStatusListener {
        return object : AdsLoadingStatusListener {
            override fun onAdRequested(adKey: String) {
                uiListener?.onAdRequested(adKey)
            }

            override fun onClicked(adKey: String) {
                uiListener?.onAdClicked(adKey)
            }

            override fun onAdLoaded(adKey: String) {
                if (adLoaded) {
                    return
                }
                adOnLoaded()
                uiListener?.onAdLoaded(adKey)
            }

            override fun onImpression(adKey: String) {
                uiListener?.onImpression(adKey)
            }

            override fun onAdFailedToLoad(adKey: String, message: String, code: Int) {
                adOnFailed()
                uiListener?.onAdFailed(adKey, message, code)
            }
        }
    }

    fun adOnLoaded() {
        adLoaded = true
        adUnit = if (showFromHistory && adsController?.getHistory()?.isNotEmpty() == true) {
            adsController?.getHistory()?.get(0)
        } else {
            adsController?.getAvailableAd()
        }
        logAds(
            "${activity?.getGoodName()} Native=On Ad Loaded,Is Ad Ok=${adUnit != null}",
            isError = adUnit == null
        )
        if (isViewInPause.not()) {
            doPopulateAd()
        }
    }

    fun adOnFailed() {
        isAdFailedToLoad = true
        if (isForRefresh.not()) {
            if (shimmerInfo.hideShimmerOnFailure) {
                makeGone()
            }
        } else if (refreshAdInfo.hideAdOnFailure) {
            if (shimmerInfo.hideShimmerOnFailure) {
                makeGone()
            }
        }
    }


    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        logAds("CustomNativeView onPause  key=$key")
        addOnPause()
    }

    private fun addOnPause() {
        isLoadAdCalled = false
        isViewInPause = true
        activity?.let { adsController?.resetListener(it) }
    }


    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        logAds("CustomNativeView onResume key=$key, isViewInPause=$isViewInPause")
        if (isViewInPause) {
            isViewInPause = false
            adOnResume()
        }
    }

    private fun adOnResume() {
        if (isShowAdCalled && isAdEnabled && isLoadAdCalled.not() && adPopulated.not()) {
            adsManager?.let {
                loadAdCalled(it)
            }
        }
    }

    private fun loadAdCalled(adsManager: AdsManager<T>) {
        isLoadAdCalled = true
        adsController = adsManager.getAdController(key)
        if (adsController == null) {
            makeGone()
            logAds("Controller for $key, is not available", true)
            uiListener?.onAdFailed(key, "Controller for $key, is not available", -1)
            return
        }
        if (adLoaded && adUnit != null) {
            doPopulateAd()
            return
        }
        if (isAdEnabled.not()) {
            makeGone()
            logAds("Ad Is Not Enabled From Firebase")
            uiListener?.onAdFailed(key, "Ad Is Not Enabled From Firebase", -1)
            return
        }
        makeVisible()
        if (shimmerInfo != ShimmerInfo.None) {
            if (isForRefresh) {
                if (refreshAdInfo.showShimmer) {
                    showShimmerLayout()
                }
            } else {
                showShimmerLayout()
            }
        }
        loadAd()
    }

    private fun doPopulateAd() {
        logAds("doPopulateAd(key=$key) isViewInPause=${isViewInPause}", isViewInPause)
        if (isViewInPause.not()) {
            adPopulated = true
            makeVisible()
            removeAllViews()
            populateAd()
        }

    }

    fun adPopulated(): Boolean {
        return adPopulated
    }

    fun setInPause(check: Boolean) {
        logAds("setInPause($key)=$check")
        isViewInPause = check
        if (isViewInPause.not()) {
            lifecycle?.let { attachLifecycle(it) }
            adOnResume()
        } else {
            removeLifecycle()
            addOnPause()
        }
    }

    fun refreshLayout() {
        Handler(Looper.getMainLooper()).postDelayed({
            rootView.requestLayout()
        }, 50)
    }


    abstract fun populateAd()
    abstract fun showShimmerLayout()
    abstract fun loadAd()

}