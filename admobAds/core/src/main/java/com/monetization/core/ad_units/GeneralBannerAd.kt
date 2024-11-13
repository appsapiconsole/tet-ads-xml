package com.monetization.core.ad_units

import android.app.Activity
import android.widget.FrameLayout
import com.monetization.core.ad_units.core.AdUnit

interface GeneralBannerAd : AdUnit {
    fun destroyAd(activity: Activity?)
    fun populateAd(activity: Activity, view: FrameLayout, onPopulated: () -> Unit)
}