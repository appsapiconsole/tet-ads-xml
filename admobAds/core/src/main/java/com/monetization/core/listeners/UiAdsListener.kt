package com.monetization.core.listeners

interface UiAdsListener {
    fun onAdClicked(key: String) {}
    fun onImpression(key: String) {}
    fun onAdRequested(key: String) {}
    fun onAdPopulated(key: String) {}
    fun onRewarded(key: String) {}
    fun onAdLoaded(key: String, mediationClassName: String?) {}
    fun onFullScreenAdShownFailed(key: String) {}
    fun onAdFailed(key: String, msg: String, code: Int, mediationClassName: String?) {}
}