package com.monetization.core.listeners

import com.monetization.core.ad_units.core.AdType

interface ControllersListener {
    fun onAdRequested(adKey: String, adType: AdType, dataMap: HashMap<String, String>){}
    fun onAdRevenue(adKey: String, adType: AdType, value: Long,currencyCode: String,precisionType: Int){}
    fun onAdLoaded(adKey: String, adType: AdType, dataMap: HashMap<String, String>){}
    fun onAdImpression(adKey: String, adType: AdType, dataMap: HashMap<String, String>){}
    fun onAdDismissed(adKey: String, adType: AdType, dataMap: HashMap<String, String>){}
    fun onFailToShow(adKey: String, adType: AdType, dataMap: HashMap<String, String>){}
    fun onAdClicked(adKey: String, adType: AdType, dataMap: HashMap<String, String>){}
    fun onAdFailed(
        adKey: String,
        adType: AdType,
        message: String,
        error: Int,
        dataMap: HashMap<String, String>
    ){}
}