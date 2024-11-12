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
            override fun onAdImpression(
                adKey: String,
                adType: AdType,
                dataMap: HashMap<String, String>
            ) {
                super.onAdImpression(adKey, adType, dataMap)
            }

            override fun onAdFailed(
                adKey: String,
                adType: AdType,
                message: String,
                error: Int,
                dataMap: HashMap<String, String>
            ) {
                super.onAdFailed(adKey, adType, message, error, dataMap)
            }

            override fun onAdLoaded(
                adKey: String,
                adType: AdType,
                dataMap: HashMap<String, String>
            ) {
                super.onAdLoaded(adKey, adType, dataMap)
            }

            override fun onAdRequested(
                adKey: String,
                adType: AdType,
                dataMap: HashMap<String, String>
            ) {

                super.onAdRequested(adKey, adType, dataMap)
            }
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