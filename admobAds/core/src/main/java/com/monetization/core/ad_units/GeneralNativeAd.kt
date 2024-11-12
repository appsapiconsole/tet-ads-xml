package com.monetization.core.ad_units

import android.app.Activity
import android.view.View
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.ui.AdsWidgetData


interface GeneralNativeAd : AdUnit {
    fun getTitle(): String?
    fun getDescription(): String?
    fun getCtaText(): String?
    fun getAdvertiserName(): String?
    fun destroyAd(activity: Activity)
    fun populateAd(
        activity: Activity,
        adViewLayout: View?,
        adsWidgetData: AdsWidgetData? = null,
        onPopulated: () -> Unit,
    )
}
