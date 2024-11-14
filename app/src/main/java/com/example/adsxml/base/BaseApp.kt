package com.example.adsxml.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.monetization.adsmain.splash.AdmobSplashAdController
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.commons.placementToAdWidgetModel
import com.monetization.core.listeners.RemoteConfigsProvider
import com.monetization.core.listeners.SdkListener
import com.monetization.core.ui.AdsWidgetData
import com.remote.firebaseconfigs.SdkRemoteConfigController
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApp : Application(), Application.ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()

        val module = module {
            single {
                AdmobSplashAdController()
            }
        }
        startKoin {
            modules(module)
            androidContext(applicationContext)
        }

        SdkConfigs.setRemoteConfigsListener(object : RemoteConfigsProvider {
            override fun isAdEnabled(placementKey: String, adKey: String, adType: AdType): Boolean {
                return SdkRemoteConfigController.getRemoteConfigBoolean(placementKey)
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

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        SdkConfigs.setActivity(p0)
    }

    override fun onActivityStarted(p0: Activity) {
        SdkConfigs.setActivity(p0)
    }

    override fun onActivityResumed(p0: Activity) {
        SdkConfigs.setActivity(p0)
    }

    override fun onActivityPaused(p0: Activity) {
        SdkConfigs.setActivity(p0)
    }

    override fun onActivityStopped(p0: Activity) {
        SdkConfigs.setActivity(null)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        SdkConfigs.setActivity(null)
    }

}