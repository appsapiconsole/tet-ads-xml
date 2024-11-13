package com.monetization.bannerads

import android.app.Activity
import com.google.android.gms.ads.AdSize

sealed class BannerAdType {

    data class Collapsible(val collapseType: BannerCollapsable) : BannerAdType()
    data class Normal(val size: BannerAdSize) : BannerAdType()

}

fun BannerAdType.getBannerSize(activity: Activity): AdSize? {
    return when (this) {
        is BannerAdType.Collapsible -> {
            return null
        }

        is BannerAdType.Normal -> {
            when (this.size) {
                BannerAdSize.MediumRectangle -> {
                    AdSize.MEDIUM_RECTANGLE
                }

                BannerAdSize.Banner -> {
                    AdSize.BANNER
                }
                BannerAdSize.AdaptiveBanner -> {
                    getAdSize(activity)
                }
            }
        }
    }
}