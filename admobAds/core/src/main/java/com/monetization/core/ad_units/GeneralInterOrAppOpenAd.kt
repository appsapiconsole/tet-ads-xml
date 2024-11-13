package com.monetization.core.ad_units

import android.app.Activity
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ad_units.core.AdUnit

interface GeneralInterOrAppOpenAd : AdUnit {
    fun showAd(activity: Activity, callBack: FullScreenAdsShowListener)
    fun destroyAd()
}