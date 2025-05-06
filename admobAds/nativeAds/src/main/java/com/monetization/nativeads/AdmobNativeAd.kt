package com.monetization.nativeads

import android.app.Activity
import com.google.android.gms.ads.nativead.NativeAd
import com.monetization.core.ad_units.GeneralNativeAd
import com.monetization.core.ad_units.core.AdType

class AdmobNativeAd(
    val adKey: String,
    val nativeAd: NativeAd,
) : GeneralNativeAd {
    override fun getTitle(): String? {
        return nativeAd.headline
    }

    override fun getDescription(): String? {
        return nativeAd.body
    }

    override fun getCtaText(): String? {
        return nativeAd.callToAction
    }

    override fun getAdvertiserName(): String? {
        return nativeAd.advertiser
    }

    override fun destroyAd(activity: Activity) {
        AdmobNativeAdsManager.getAdController(adKey)?.destroyAd(activity)
    }


    override fun getAdType(): AdType {
        return AdType.NATIVE
    }
}