package com.monetization.nativeads.populate

import android.app.Activity
import android.view.View
import com.google.android.gms.ads.nativead.NativeAd
import com.monetization.core.ui.AdsWidgetData

interface NativePopulator {

    fun populateAd(
        activity: Activity,
        nativeAd: NativeAd?,
        adViewLayout: View?,
        adsWidgetData: AdsWidgetData?,addViewInParent: (View) -> Unit,
        onPopulated: () -> Unit,
    )
}