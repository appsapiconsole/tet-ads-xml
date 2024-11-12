package com.monetization.adsmain.sdk

import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.appopen.AppOpenListener

object AdmobAppOpenAdsHelper {
    fun initOpensAds(
        onShowAppOpenAd: () -> Unit,
        canShowAppOpenAd: () -> Boolean
    ) {
        AdmobAppOpenAdsManager.initAppOpen(object : AppOpenListener {
            override fun onShowAd() {
                if (canShowAppOpenAd.invoke()) {
                    onShowAppOpenAd.invoke()
                }
            }
        })
    }
}