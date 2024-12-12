package com.monetization.bannerads

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.controllers.ControllerState
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.provider.ad_request.AdRequestProvider
import com.monetization.core.provider.ad_request.DefaultAdRequestProvider

class AdmobBannerAdsController(
    adKey: String,
    adIdsList: List<String>,
    private val bannerAdType: BannerAdType = BannerAdType.Normal(BannerAdSize.AdaptiveBanner),
    listener: ControllersListener? = null,
    private val adRequestProvider: AdRequestProvider = DefaultAdRequestProvider()
) : AdsControllerBaseHelper(
    adKey = adKey,
    adType = AdType.BANNER,
    adIdsList = adIdsList,
    listener = listener
) {

    private var currentBannerAd: AdmobBannerAd? = null


    fun getBannerAdType() = bannerAdType

    override fun loadAd(
        placementKey: String,
        activity: Activity,
        calledFrom: String,
        callback: AdsLoadingStatusListener?
    ) {
        val commonLoadChecks = commonLoadAdChecks(placementKey = placementKey, callback = callback)
        if (commonLoadChecks.not()) {
            return
        }
        val adId = getAdIdAndIncrementIndex()
        val adView = AdView(activity);
        adView.adUnitId = adId
        val extras = Bundle()
        val adSize = bannerAdType.getBannerSize(activity)
        if (adSize == null) {
            (bannerAdType as? BannerAdType.Collapsible)?.let {
                val size = BannerAdType.Normal(BannerAdSize.AdaptiveBanner).getBannerSize(activity)
                    ?: AdSize.BANNER
                adView.setAdSize(size)
                extras.putString(
                    "collapsible", when (bannerAdType.collapseType) {
                        BannerCollapsable.CollapseTop -> {
                            "top"
                        }

                        BannerCollapsable.CollapseBottom -> {
                            "bottom"
                        }
                    }
                )
            }
        } else {
            adView.setAdSize(adSize)
        }
        val adRequest = adRequestProvider.getAdRequest()

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                currentBannerAd?.destroyAd(activity)
                currentBannerAd = AdmobBannerAd(getAdKey(), adView)
                currentBannerAd?.adView?.setOnPaidEventListener { paidListener ->
                    val loadedAdapterResponseInfo = adView.responseInfo?.loadedAdapterResponseInfo
                    val adSourceName: String? = loadedAdapterResponseInfo?.adSourceName
                    val adSourceId: String? = loadedAdapterResponseInfo?.adSourceId
                    val adSourceInstanceName: String? =
                        loadedAdapterResponseInfo?.adSourceInstanceName
                    val adSourceInstanceId: String? =
                        loadedAdapterResponseInfo?.adSourceInstanceId
                    onAdRevenue(
                        value = paidListener.valueMicros,
                        currencyCode = paidListener.currencyCode,
                        precisionType = paidListener.precisionType,
                        adSourceName = adSourceName,
                        adSourceId = adSourceId,
                        adSourceInstanceName = adSourceInstanceName,
                        adSourceInstanceId = adSourceInstanceId,
                        extras = adView.responseInfo?.responseExtras
                    )
                }
                onLoaded(adView.responseInfo?.mediationAdapterClassName)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                currentBannerAd = null
                onAdFailed(error)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                onImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                onAdClick()
            }
        }
        adView.loadAd(adRequest)
        onAdRequested()
    }

    override fun destroyAd(activity: Activity?) {
        currentBannerAd = null
        setControllerState(ControllerState.NoAdAvailable)
    }

    override fun getAvailableAd(): AdUnit? {
        return currentBannerAd
    }

}