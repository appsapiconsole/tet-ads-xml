package com.monetization.core.managers

interface AdsLoadingStatusListener {
    fun onAdRequested(adKey: String) {}
    fun onAdLoaded(adKey: String) {}
    fun onImpression(adKey: String) {}
    fun onClicked(adKey: String) {}
    fun onAdFailedToLoad(adKey: String, message: String = "", code: Int = -1) {}
}