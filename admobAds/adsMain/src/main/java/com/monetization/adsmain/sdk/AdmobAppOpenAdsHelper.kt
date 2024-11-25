package com.monetization.adsmain.sdk

import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.appopen.AppOpenAndLifecycleListener

object AdmobAppOpenAdsHelper {
    fun initOpensAds(
        onShowAppOpenAd: () -> Unit,
        canShowAppOpenAd: () -> Boolean,
        listener: AppOpenAndLifecycleListener? = null
    ) {
        AdmobAppOpenAdsManager.initAppOpen(object : AppOpenAndLifecycleListener {
            override fun onAppStart() {
                if (canShowAppOpenAd.invoke()) {
                    onShowAppOpenAd.invoke()
                }
                listener?.onAppStart()
            }

            override fun onAppCreate() {
                listener?.onAppCreate()
            }

            override fun onAppStop() {
                listener?.onAppStop()
            }

            override fun onAppResume() {
                listener?.onAppResume()
            }

            override fun onAppDestroy() {
                listener?.onAppDestroy()
            }

            override fun onAppPause() {

            }

            override fun onAppStopped() {

            }
        })
    }
}