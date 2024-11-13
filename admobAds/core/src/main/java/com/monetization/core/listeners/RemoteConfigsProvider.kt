package com.monetization.core.listeners

import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ui.AdsWidgetData

interface RemoteConfigsProvider {
    fun isAdEnabled(placementKey: String, adKey: String, adType: AdType): Boolean
    fun getAdWidgetData(placementKey: String, adKey: String): AdsWidgetData?
}