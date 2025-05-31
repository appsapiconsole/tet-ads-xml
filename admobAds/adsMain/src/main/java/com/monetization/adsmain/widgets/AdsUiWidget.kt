package com.monetization.adsmain.widgets

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import com.monetization.adsmain.commons.isAdAvailable
import com.monetization.bannerads.ui.BannerAdWidget
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs.getRemoteAdWidgetModel
import com.monetization.core.commons.SdkConfigs.isRemoteAdEnabled
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.models.RefreshAdInfo
import com.monetization.core.ui.AdsWidgetData
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo
import com.monetization.nativeads.populate.NativePopulator
import com.monetization.nativeads.populate.NativePopulatorImpl
import com.monetization.nativeads.ui.NativeAdWidget
import video.downloader.adsmain.R

class AdsUiWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), DefaultLifecycleObserver {

    private var funAdKey: String? = null

    init {
        logAds("AdWidget called", true)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AdsUiWidget,
            defStyleAttr,
            0
        ).apply {
            try {
                funAdKey = getString(R.styleable.AdsUiWidget_fun_ad_Key)
                logAds("Placement Key = $funAdKey")
            } finally {
                recycle()
            }
        }
    }

    fun getFunAdKey(): String? {
        return funAdKey
    }


    private var nativeWidget: NativeAdWidget =
        NativeAdWidget(context, attrs, defStyleAttr)
    private var bannerWidget: BannerAdWidget =
        BannerAdWidget(context, attrs, defStyleAttr)

    private var placementKey: String? = null
    private var adEnabled: Boolean = false
    private var adsWidgetDataModel: AdsWidgetData? = null

    fun getNativeWidget(): NativeAdWidget {
        return nativeWidget
    }

    fun showShimmerView(
        shimmer: ShimmerInfo?, activity: Activity, forNative: Boolean
    ) {
        if (forNative) {
            removeAndAdNativeWidget()
            nativeWidget.showShimmerLayout(shimmer, activity)
        } else {
            removeAndAdBannerWidget()
            bannerWidget.showShimmerLayout(shimmer, activity)
        }
    }

    fun getBannerWidget() = bannerWidget

    fun setLifecycle(lifecycle: Lifecycle, isNativeAd: Boolean) {
        if (isNativeAd) {
            nativeWidget.attachLifecycle(lifecycle)
        } else {
            bannerWidget.attachLifecycle(lifecycle)
        }
    }

    fun canRefreshAdView(check: Boolean) {
        nativeWidget.canRefreshAdLayout(check)
        bannerWidget.canRefreshAdLayout(check)
    }

    fun removeLifecycle() {
        nativeWidget.removeLifecycle()
        bannerWidget.removeLifecycle()
    }

    fun isAdPopulated(forNative: Boolean): Boolean {
        return if (forNative) {
            nativeWidget.adPopulated()
        } else {
            bannerWidget.adPopulated()
        }
    }

    fun setRepeatCheckingAllowed(check: Boolean) {
        nativeWidget.setRepeatCheckingAllowed(check)
    }

    fun setWidgetKey(
        placementKey: String,
        adKey: String,
        isNativeAd: Boolean,
        model: AdsWidgetData?,
        defEnabled: Boolean = true
    ) {
        this.placementKey = placementKey
        if (this.placementKey == null || this.placementKey?.isEmpty() == true) {
            throw IllegalArgumentException("Please Pass Placement Key")
        }

        val (enabled, widgetModel) = Pair(
            placementKey.isRemoteAdEnabled(
                key = adKey, adType = if (isNativeAd) {
                    AdType.NATIVE
                } else {
                    AdType.BANNER
                }, def = defEnabled
            ),
            placementKey.getRemoteAdWidgetModel(adKey, model)
        )
        bannerWidget.setWidgetKey(key = placementKey)
        nativeWidget.setWidgetKey(key = placementKey)
        adEnabled = enabled
        adsWidgetDataModel = widgetModel
        nativeWidget.setAdsWidgetData(
            adsWidgetData = adsWidgetDataModel,
            isValuesFromRemote = widgetModel.toString() != model.toString()
        )
    }

    fun attachWithLifecycle(
        lifecycle: Lifecycle,
        forBanner: Boolean = false,
        isJetpackCompose: Boolean
    ) {
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            if (forBanner) {
                bannerWidget.attachWithLifecycle(lifecycle, isJetpackCompose)
            } else {
                nativeWidget.attachWithLifecycle(lifecycle, isJetpackCompose)
            }
        }
    }

    fun showNativeAdmob(
        activity: Activity,
        adLayout: LayoutInfo,
        adKey: String,
        shimmerInfo: ShimmerInfo = ShimmerInfo.GivenLayout(),
        oneTimeUse: Boolean = true,
        requestNewOnShow: Boolean = true,
        showOnlyIfAdAvailable: Boolean = false,
        showFromHistory: Boolean = false,
        listener: UiAdsListener? = null,
        populator: NativePopulator
    ) {
        if (showOnlyIfAdAvailable && adKey.isAdAvailable().not()) {
            logAds("Because No Ad Is Available Against key=$adKey", true)
            listener?.onAdFailed(
                key = adKey,
                msg = "Because No Ad Is Available Against key=$adKey",
                code = -1,
                mediationClassName = null
            )
            return
        }
        removeAndAdNativeWidget()
        try {
            nativeWidget.showNativeAdmob(
                activity = activity,
                adKey = adKey,
                adLayout = adLayout,
                enabled = adEnabled,
                shimmerInfo = shimmerInfo,
                oneTimeUse = oneTimeUse,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showFromHistory = showFromHistory,
                adPopulator = populator
            )
        } catch (e: Exception) {
            logAds("Exception When Calling showNativeAdmob ${e.message}")
        }
    }


    fun showBannerAdmob(
        activity: Activity,
        adKey: String,
        shimmerInfo: ShimmerInfo = ShimmerInfo.GivenLayout(),
        oneTimeUse: Boolean = true,
        requestNewOnShow: Boolean = true,
        showOnlyIfAdAvailable: Boolean = true,
        listener: UiAdsListener? = null
    ) {
        if (showOnlyIfAdAvailable && adKey.isAdAvailable().not()) {
            listener?.onAdFailed(
                key = adKey,
                msg = "Because No Ad Is Available Against key=$adKey",
                code = -1,
                mediationClassName = null
            )
            return
        }
        removeAndAdBannerWidget()
        try {
            bannerWidget.showBannerAdmob(
                activity = activity,
                adKey = adKey,
                enabled = adEnabled,
                shimmerInfo = shimmerInfo,
                oneTimeUse = oneTimeUse,
                requestNewOnShow = requestNewOnShow,
                listener = listener
            )
        } catch (e: Exception) {
            logAds("Exception When Calling showBannerAdmob ${e.message}")
        }
    }

    private fun removeAndAdNativeWidget() {
        try {
            (nativeWidget.parent as? ViewGroup)?.removeView(nativeWidget)
        } catch (_: Exception) {
        }
        try {
            addView(nativeWidget)
        } catch (_: Exception) {
        }
    }

    private fun removeAndAdBannerWidget() {
        try {
            (bannerWidget.parent as? ViewGroup)?.removeView(bannerWidget)
        } catch (_: Exception) {
        }
        try {
            addView(bannerWidget)
        } catch (_: Exception) {
        }
    }

    fun setInPause(check: Boolean, forBanner: Boolean) {
        if (forBanner) {
            bannerWidget.setInPause(check)
        } else {
            nativeWidget.setInPause(check)
        }
    }

    fun refreshAd(isNativeAd: Boolean, refreshAdInfo: RefreshAdInfo = RefreshAdInfo()) {
        if (isNativeAd) {
            nativeWidget.refreshAd(refreshAdInfo)
        } else {
            bannerWidget.refreshAd(refreshAdInfo)
        }
    }

}