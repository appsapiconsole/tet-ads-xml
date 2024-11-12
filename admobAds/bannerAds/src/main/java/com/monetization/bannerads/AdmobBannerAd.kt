package com.monetization.bannerads

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.AdView
import com.monetization.core.ad_units.GeneralBannerAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds

class AdmobBannerAd(
    private val adKey: String,
    val adView: AdView,
) : GeneralBannerAd {

    override fun destroyAd(activity: Activity?) {
        AdmobBannerAdsManager.getAdController(adKey)?.destroyAd(activity)
    }

    override fun populateAd(activity: Activity, view: FrameLayout, onPopulated: () -> Unit) {
        // Remove the adView from its current parent if it has one
        adView.parent?.let { parent ->
            (parent as? ViewGroup)?.removeView(adView)
        }
        view.addView(adView)
        onPopulated.invoke()
        logAds("Banner Ad populateAd Done")
    }

    override fun getAdType(): AdType {
        return AdType.BANNER
    }
}