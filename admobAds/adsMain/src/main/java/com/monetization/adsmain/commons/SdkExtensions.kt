package com.monetization.adsmain.commons

import android.app.Activity
import android.view.View
import androidx.lifecycle.Lifecycle
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.ui.AdsWidgetData
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo
import com.monetization.nativeads.populate.NativePopulator
import com.monetization.nativeads.populate.NativePopulatorImpl


fun AdsUiWidget.sdkNativeAdd(
    adLayout: LayoutInfo,
    adKey: String,
    placementKey: String,
    lifecycle: Lifecycle,
    activity: Activity,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    showOnlyIfAdAvailable: Boolean = false,
    showFromHistory: Boolean = false,
    defaultEnable: Boolean = true,
    adsWidgetData: AdsWidgetData? = null,
    listener: UiAdsListener? = null,
    populator: NativePopulator = NativePopulatorImpl()
) {
    apply {
        attachWithLifecycle(lifecycle = lifecycle, forBanner = false, isJetpackCompose = false)
        setWidgetKey(
            placementKey = placementKey,
            adKey = adKey,
            model = adsWidgetData,
            defEnabled = defaultEnable,
            isNativeAd = true
        )
        showNativeAdmob(
            activity = activity,
            adLayout = adLayout,
            adKey = adKey,
            shimmerInfo = showShimmerLayout,
            oneTimeUse = showNewAdEveryTime,
            requestNewOnShow = requestNewOnShow,
            listener = listener,
            showOnlyIfAdAvailable = showOnlyIfAdAvailable,
            showFromHistory = showFromHistory,
            populator = populator
        )
    }
}

fun AdsUiWidget.sdkNativeAd(
    adLayout: String,
    adKey: String,
    placementKey: String,
    lifecycle: Lifecycle,
    activity: Activity,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    showOnlyIfAdAvailable: Boolean = false,
    showFromHistory: Boolean = false,
    defaultEnable: Boolean = true,
    adsWidgetData: AdsWidgetData? = null,
    listener: UiAdsListener? = null
) {
    sdkNativeAdd(
        adLayout = LayoutInfo.LayoutByName(adLayout),
        adKey = adKey,
        placementKey = placementKey,
        lifecycle = lifecycle,
        activity = activity,
        showShimmerLayout = showShimmerLayout,
        requestNewOnShow = requestNewOnShow,
        showNewAdEveryTime = showNewAdEveryTime,
        showOnlyIfAdAvailable = showOnlyIfAdAvailable,
        showFromHistory = showFromHistory,
        defaultEnable = defaultEnable,
        adsWidgetData = adsWidgetData,
        listener = listener
    )
}

fun AdsUiWidget.sdkBannerAd(
    adKey: String,
    placementKey: String,
    lifecycle: Lifecycle,
    activity: Activity,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    showOnlyIfAdAvailable: Boolean = false,
    defaultEnable: Boolean = true,
    listener: UiAdsListener? = null
) {
    apply {
        attachWithLifecycle(lifecycle = lifecycle, forBanner = true, isJetpackCompose = false)
        setWidgetKey(
            placementKey = placementKey, adKey = adKey,
            model = null,
            defEnabled = defaultEnable,
            isNativeAd = false
        )
        showBannerAdmob(
            activity = activity,
            adKey = adKey,
            shimmerInfo = showShimmerLayout,
            oneTimeUse = showNewAdEveryTime,
            requestNewOnShow = requestNewOnShow,
            listener = listener,
            showOnlyIfAdAvailable = showOnlyIfAdAvailable
        )
    }
}
