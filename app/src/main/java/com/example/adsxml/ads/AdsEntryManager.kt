package com.example.adsxml.ads

import android.content.Context
import com.monetization.adsmain.commons.addNewController
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.listeners.ControllersListener
import com.monetization.interstitials.AdmobInterstitialAdsManager
import com.monetization.nativeads.AdmobNativeAdsManager

object AdsEntryManager {


    fun initAds(context: Context) {
        val listener = object : ControllersListener {
        }
        AdmobInterstitialAdsManager.addNewController(
            adKey = "Main",
            adIdsList = listOf(""),
            listener = listener
        )
        AdmobNativeAdsManager.addNewController(
            adKey = "Main",
            adIdsList = listOf(""),
            listener = listener
        )

    }
}