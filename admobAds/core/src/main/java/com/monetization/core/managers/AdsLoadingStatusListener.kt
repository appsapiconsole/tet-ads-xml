package com.monetization.core.managers

import com.monetization.core.controllers.AdsControllerBaseHelper.AdapterResponses

interface AdsLoadingStatusListener {
    fun onAdRequested(adKey: String) {}
    fun onAdLoaded(adKey: String,mediationClassName: String?) {}
    fun onImpression(adKey: String) {}
    fun onClicked(adKey: String) {}
    fun onAdFailedToLoad(
        adKey: String,
        message: String = "",
        code: Int = -1,
        mediationClassName: String? ,
        adapterResponses : List<AdapterResponses>?
    ) {}
}