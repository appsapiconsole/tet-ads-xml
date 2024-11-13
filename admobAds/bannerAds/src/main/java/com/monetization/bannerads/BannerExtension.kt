package com.monetization.bannerads

import android.app.Activity
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.DisplayMetrics
import android.view.WindowMetrics
import com.google.android.gms.ads.AdSize


fun getAdSize(activity: Activity): AdSize {
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    var adWidthPixels = displayMetrics.widthPixels

    if (VERSION.SDK_INT >= VERSION_CODES.R) {
        val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
        adWidthPixels = windowMetrics.bounds.width()
    }

    val density = displayMetrics.density
    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
}