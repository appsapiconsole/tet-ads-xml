package com.monetization.adsmain.sdk

import android.util.Log
import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.appopen.AppOpenAndLifecycleListener

object AdmobAppOpenAdsHelper {
    fun initOpensAds(
        onShowAppOpenAd: () -> Unit,
        canShowAppOpenAd: () -> Boolean,
        listener: AppOpenAndLifecycleListener? = null
    ) {
        Log.d(AdmobAppOpenAdsManager.TAG, "initOpensAds: ")
        AdmobAppOpenAdsManager.initAppOpen(object : AppOpenAndLifecycleListener {
            override fun onAppStart() {
                val canShow = canShowAppOpenAd.invoke()
                Log.d(AdmobAppOpenAdsManager.TAG, "onAppStart canShow = $canShow")
                if (canShow) {
                    onShowAppOpenAd.invoke()
                }
                listener?.onAppStart()
            }

            override fun onAppCreate() {
                Log.d(AdmobAppOpenAdsManager.TAG, "onAppCreate: ")
                listener?.onAppCreate()
            }

            override fun onAppStop() {
                Log.d(AdmobAppOpenAdsManager.TAG, "onAppStop: ")
                listener?.onAppStop()
            }

            override fun onAppResume() {
                Log.d(AdmobAppOpenAdsManager.TAG, "onAppResume: ")
                listener?.onAppResume()
            }

            override fun onAppDestroy() {
                Log.d(AdmobAppOpenAdsManager.TAG, "onAppDestroy: ")
                listener?.onAppDestroy()
            }

            override fun onAppPause() {
                Log.d(AdmobAppOpenAdsManager.TAG, "onAppPause: ")

            }

            override fun onAppStopped() {
                Log.d(AdmobAppOpenAdsManager.TAG, "onAppStopped: ")

            }
        })
    }
}