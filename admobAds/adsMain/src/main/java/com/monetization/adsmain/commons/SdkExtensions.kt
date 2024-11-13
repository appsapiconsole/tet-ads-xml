package com.monetization.adsmain.commons

import android.app.Activity
import androidx.lifecycle.Lifecycle
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.ui.AdsWidgetData
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo


fun AdsUiWidget.sdkNativeAd(
    adLayout: String,
    adKey: String,
    placementKey: String,
    lifecycle: Lifecycle,
    activity: Activity? = SdkConfigs.getCurrentActivityRef(),
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    showOnlyIfAdAvailable: Boolean = false,
    showFromHistory: Boolean = false,
    defaultEnable: Boolean = true,
    adsWidgetData: AdsWidgetData? = null,
    listener: UiAdsListener? = null
) {
    if (activity == null) {
        "Pass Activity When Calling sdkNativeAd".errorLogging()
    } else {
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
                adLayout = LayoutInfo.LayoutByName(adLayout),
                adKey = adKey,
                shimmerInfo = showShimmerLayout,
                oneTimeUse = showNewAdEveryTime,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showOnlyIfAdAvailable = showOnlyIfAdAvailable,
                showFromHistory = showFromHistory
            )
        }
    }
}

fun AdsUiWidget.sdkBannerAd(
    adKey: String,
    placementKey: String,
    lifecycle: Lifecycle,
    activity: Activity? = SdkConfigs.getCurrentActivityRef(),
    type: BannerAdType = BannerAdType.Normal(BannerAdSize.AdaptiveBanner),
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    showOnlyIfAdAvailable: Boolean = false,
    defaultEnable: Boolean = true,
    listener: UiAdsListener? = null
) {
    if (activity == null) {
        "Pass Activity When Calling sdkBannerAd".errorLogging()
    } else {
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
                bannerAdType = type,
                adKey = adKey,
                shimmerInfo = showShimmerLayout,
                oneTimeUse = showNewAdEveryTime,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showOnlyIfAdAvailable = showOnlyIfAdAvailable
            )
        }
    }
}
