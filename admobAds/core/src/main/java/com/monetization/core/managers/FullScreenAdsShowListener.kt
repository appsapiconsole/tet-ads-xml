package com.monetization.core.managers

interface FullScreenAdsShowListener {
    fun onAdDismiss(adKey: String, adShown: Boolean = false, rewardEarned: Boolean = false) {}
    fun onAdShown(adKey: String) {}
    fun onAdLoaded(adKey: String) {}
    fun splashAdOnPause() {}
    fun splashAdOnResume() {}
    fun onAdShownFailed(adKey: String) {}
    fun onAdClick(adKey: String) {}
    fun onRewarded(adKey: String) {}
    fun onShowBlackBg(adKey: String, show: Boolean) {}
}