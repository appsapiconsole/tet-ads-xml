package com.monetization.adsmain.showRates.natives

import android.app.Activity
import androidx.lifecycle.Lifecycle
import com.monetization.adsmain.commons.isAdAvailable
import com.monetization.adsmain.commons.isAdAvailableOrRequesting
import com.monetization.adsmain.commons.isAdRequesting
import com.monetization.adsmain.showRates.inter.showRatesHelper
import com.monetization.adsmain.showRates.models.IgnoreNewRequest
import com.monetization.adsmain.showRates.models.ignoreIfAnyOtherLoadedAd
import com.monetization.adsmain.widgets.AdsUiWidget
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.showRates.ShowRatesCommons
import com.monetization.core.ui.AdsWidgetData
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo


fun AdsUiWidget.sdkNativeAdSR(
    activity: Activity,
    adLayout: String,
    adKey: String,
    placementKey: String,
    lifecycle: Lifecycle,
    showShimmerLayout: ShimmerInfo = ShimmerInfo.GivenLayout(),
    requestNewOnShow: Boolean = false,
    showNewAdEveryTime: Boolean = true,
    showOnlyIfAdAvailable: Boolean = false,
    defaultEnable: Boolean = true,
    ignoreHistory: Boolean = false,
    ignoreNewRequest: IgnoreNewRequest = ignoreIfAnyOtherLoadedAd(AdType.NATIVE),
    adsWidgetData: AdsWidgetData? = null,
    listener: UiAdsListener? = null
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

        val adsWithHistory = showRatesHelper.getNativeControllersWithHistory()
        val alreadyLoadedOrRequesting =
            showRatesHelper.getLoadedOrRequestingAdsControllersByType(AdType.NATIVE)

        val adAvailable = adKey.isAdAvailable(AdType.NATIVE)
        val adRequesting = adKey.isAdRequesting(AdType.NATIVE)
        val adsWillingToBeUsed = when (ignoreNewRequest) {
            IgnoreNewRequest.DontIgnore -> {
                listOf()
            }

            is IgnoreNewRequest.IfTheseAvailable -> {
                ignoreNewRequest.ids.filter {
                    it.isAdAvailable(AdType.NATIVE)
                }
            }

            is IgnoreNewRequest.IfTheseRequesting -> {
                ignoreNewRequest.ids.filter {
                    it.isAdRequesting(AdType.NATIVE)
                }
            }

            is IgnoreNewRequest.IfTheseRequestingOrLoaded -> {
                ignoreNewRequest.ids.filter {
                    it.isAdAvailableOrRequesting(AdType.NATIVE)
                }
            }
        }
        if (adAvailable || adRequesting) {
            showNativeAdmob(
                activity = activity,
                adLayout = LayoutInfo.LayoutByName(adLayout),
                adKey = adKey,
                shimmerInfo = showShimmerLayout,
                oneTimeUse = showNewAdEveryTime,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showOnlyIfAdAvailable = showOnlyIfAdAvailable
            )
        } else if (adsWillingToBeUsed.isNotEmpty()) {
            showNativeAdmob(
                activity = activity,
                adLayout = LayoutInfo.LayoutByName(adLayout),
                adKey = adsWillingToBeUsed[0],
                shimmerInfo = showShimmerLayout,
                oneTimeUse = showNewAdEveryTime,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showOnlyIfAdAvailable = showOnlyIfAdAvailable
            )
        } else if (
            ShowRatesCommons.getBestShowRates().canRequestNewAd(
                adType = AdType.NATIVE,
                size = alreadyLoadedOrRequesting.size
            )
        ) {
            showNativeAdmob(
                activity = activity,
                adLayout = LayoutInfo.LayoutByName(adLayout),
                adKey = adKey,
                shimmerInfo = showShimmerLayout,
                oneTimeUse = showNewAdEveryTime,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showOnlyIfAdAvailable = showOnlyIfAdAvailable
            )
        } else if (adsWithHistory.isNotEmpty() && ignoreHistory.not()) {
            showNativeAdmob(
                activity = activity,
                adLayout = LayoutInfo.LayoutByName(adLayout),
                adKey = adsWithHistory[0].getAdKey(),
                shimmerInfo = showShimmerLayout,
                oneTimeUse = showNewAdEveryTime,
                requestNewOnShow = requestNewOnShow,
                listener = listener,
                showOnlyIfAdAvailable = showOnlyIfAdAvailable,
                showFromHistory = true
            )
        } else {
            logAds("Unable To Show Ad Because Limit Reached", true)
        }
    }
}