package com.monetization.core.listeners

import android.os.Bundle
import com.monetization.core.ad_units.core.AdType

interface ControllersListener {
    fun onAdRequested(
        adKey: String,
        adType: AdType,
        placementKey: String,
        dataMap: HashMap<String, String>
    ) {
    }

    fun onAdRevenue(
        adKey: String,
        adType: AdType,
        value: Long,
        currencyCode: String,
        precisionType: Int,
        adSourceName: String?,
        adSourceId: String?,
        adSourceInstanceName: String?,
        adSourceInstanceId: String?,
        extras: Bundle?
    ) {
    }

    fun onAdLoaded(
        adKey: String,
        adType: AdType,
        placementKey: String,
        dataMap: HashMap<String, String>,
        mediationClassName: String?
    ) {
    }

    fun onAdImpression(
        adKey: String,
        adType: AdType,
        placementKey: String,
        dataMap: HashMap<String, String>
    ) {
    }

    fun onAdDismissed(
        adKey: String,
        adType: AdType,
        placementKey: String,
        dataMap: HashMap<String, String>
    ) {
    }

    fun onFailToShow(
        adKey: String,
        adType: AdType,
        placementKey: String,
        dataMap: HashMap<String, String>
    ) {
    }

    fun onAdClicked(
        adKey: String,
        adType: AdType,
        placementKey: String,
        dataMap: HashMap<String, String>
    ) {
    }

    fun onAdShown(
        adKey: String,
        adType: AdType,
        placementKey: String,
        dataMap: HashMap<String, String>
    ) {
    }

    fun onAdFailed(
        adKey: String,
        adType: AdType,
        message: String,
        error: Int,
        placementKey: String,
        dataMap: HashMap<String, String>
    ) {
    }
}