package com.example.adsxml.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.monetization.adsmain.sdk.AdmobAppOpenAdsHelper
import com.monetization.adsmain.showRates.full_screen_ads.FullScreenAdsShowManager
import com.monetization.adsmain.showRates.full_screen_ads.toInstantAd
import com.monetization.adsmain.splash.AdmobSplashAdController
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.commons.placementToAdWidgetModel
import com.monetization.core.listeners.RemoteConfigsProvider
import com.monetization.core.listeners.SdkDialogsListener
import com.monetization.core.listeners.SdkListener
import com.monetization.core.msgs.MessagesType
import com.monetization.core.ui.AdsWidgetData
import com.monetization.core.utils.dialog.SdkDialogs
import com.monetization.core.utils.dialog.onAdLoadingDialogStateChange
import com.remote.firebaseconfigs.RemoteCommons.toConfigString
import com.remote.firebaseconfigs.SdkRemoteConfigController
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApp : Application(), Application.ActivityLifecycleCallbacks {

    private var canShowAppOpenAd = false
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val module = module {
            single {
                AdmobSplashAdController()
            }
        }
        startKoin {
            modules(module)
            androidContext(applicationContext)
        }

        var sdkDialog: SdkDialogs? = null
        SdkConfigs.setDialogListener(object : SdkDialogsListener {
            override fun onAdLoadingDialogStateChange(
                activity: Activity,
                placementKey: String,
                adKey: String,
                adType: AdType,
                showDialog: Boolean,
                isForBlackBg: Boolean
            ) {
                if (showDialog) {
                    sdkDialog = SdkDialogs(activity)
                }
                sdkDialog?.onAdLoadingDialogStateChange(
                    showDialog = showDialog,
                    isForBlackBg = isForBlackBg
                )
            }
        })

        SdkConfigs.setRemoteConfigsListener(object : RemoteConfigsProvider {
            override fun isAdEnabled(placementKey: String, adKey: String, adType: AdType): Boolean {
//                return SdkRemoteConfigController.getRemoteConfigBoolean(placementKey)
                return true
            }

            override fun getAdWidgetData(placementKey: String, adKey: String): AdsWidgetData? {
                return SdkRemoteConfigController.getRemoteConfigString(placementKey + "_Placement")
                    .placementToAdWidgetModel()
            }
        })

        SdkConfigs.setListener(listener = object : SdkListener {
            override fun canShowAd(adType: AdType, placementKey: String, adKey: String): Boolean {
                return true
            }

            override fun canLoadAd(adType: AdType, placementKey: String, adKey: String): Boolean {
                return true
            }
        }, testModeEnable = true)
        registerActivityLifecycleCallbacks(this)

    }

    companion object {
        var appContext: Context? = null
    }

    fun showAppOpenAd() {
        registerActivityLifecycleCallbacks(this)
        AdmobAppOpenAdsHelper.initOpensAds(
            onShowAppOpenAd = {
                FullScreenAdsShowManager.showFullScreenAd(
                    placementKey = true.toConfigString(),
                    activity = currentActivity!!,
                    key = "AppOpen",
                    counterKey = null,
                    showOptions = true.toInstantAd(),
                    onAdDismiss = { adShown: Boolean, msgType: MessagesType? ->

                    },
                    adType = AdType.AppOpen
                )
            },
            canShowAppOpenAd = {
                canShowAppOpenAd && currentActivity != null
            }
        )
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        currentActivity = p0
        canShowAppOpenAd = true
    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
        canShowAppOpenAd = true
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
        canShowAppOpenAd = true
    }

    override fun onActivityPaused(p0: Activity) {
        canShowAppOpenAd = false
    }

    override fun onActivityStopped(p0: Activity) {
        currentActivity = null
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
        currentActivity = null
        canShowAppOpenAd = false
    }

}