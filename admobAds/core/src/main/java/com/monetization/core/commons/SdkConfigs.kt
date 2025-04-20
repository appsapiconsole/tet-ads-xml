package com.monetization.core.commons

import android.annotation.SuppressLint
import android.app.Activity
import com.facebook.shimmer.BuildConfig
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.listeners.RemoteConfigsProvider
import com.monetization.core.listeners.SdkDialogsListener
import com.monetization.core.listeners.SdkListener
import com.monetization.core.models.ManualBlockModel
import com.monetization.core.ui.AdsWidgetData

@SuppressLint("StaticFieldLeak")
object SdkConfigs {

    private var isTestModeEnabled = BuildConfig.DEBUG
    private var disableAllAds = false
    private var manualBlockList = mutableListOf<ManualBlockModel>()

    fun isAdManuallyBlockedForLoad(adKey: String, adType: AdType): Boolean {
        val index = manualBlockList.indexOfFirst {
            it.adKey == adKey && adType == it.adType
        }
        return if (index != -1) {
            manualBlockList[index].blockForLoad
        } else {
            false
        }
    }

    fun isAdManuallyBlockedOverAll(adKey: String, adType: AdType) =
        isAdManuallyBlockedForLoad(adKey, adType) && isAdManuallyBlockedForShow(adKey, adType)

    fun isAdManuallyBlockedForShow(adKey: String, adType: AdType): Boolean {
        val index = manualBlockList.indexOfFirst {
            it.adKey == adKey && adType == it.adType
        }
        return if (index != -1) {
            manualBlockList[index].blockForShow
        } else {
            false
        }
    }

    fun unBlockAd(adKey: String, adType: AdType) {
        val index = manualBlockList.indexOfFirst {
            it.adKey == adKey && it.adType == adType
        }
        if (index != -1) {
            manualBlockList.removeAt(index)
        }
    }

    fun blockAd(
        adKey: String,
        adType: AdType,
        blockForShow: Boolean = true,
        blockForLoad: Boolean = true
    ) {
        val index = manualBlockList.indexOfFirst {
            it.adKey == adKey && it.adType == adType
        }
        if (blockForLoad || blockForShow) {
            if (index == -1) {
                manualBlockList.add(
                    ManualBlockModel(
                        adKey = adKey,
                        adType = adType,
                        blockForShow = blockForShow,
                        blockForLoad = blockForLoad
                    )
                )
            } else {
                manualBlockList[index] = ManualBlockModel(
                    adKey = adKey,
                    adType = adType,
                    blockForShow = blockForShow,
                    blockForLoad = blockForLoad
                )
            }
        }
    }

    fun isTestMode(): Boolean {
        return isTestModeEnabled
    }

    fun setTestModeEnabled(enable: Boolean) {
        isTestModeEnabled = enable
    }

    fun disableAllAds(disableAds: Boolean = true) {
        disableAllAds = disableAds
    }


    private var sdkListener: SdkListener? = null
    private var sdkDialogsListener: SdkDialogsListener? = null
    private var configListener: RemoteConfigsProvider? = null

    fun setRemoteConfigsListener(listener: RemoteConfigsProvider) {
        configListener = listener
    }

    fun String.isRemoteAdEnabled(key: String, adType: AdType, def: Boolean = true): Boolean {
        if (configListener == null) {
            throw IllegalArgumentException("Please set Remote Config Listener by call setRemoteConfigsListener(this)")
        }
        return configListener?.isAdEnabled(this, key, adType) ?: def
    }

    fun String.getRemoteAdWidgetModel(
        key: String,
        model: AdsWidgetData? = null
    ): AdsWidgetData? {
        if (configListener == null) {
            throw IllegalArgumentException("Please set Remote Config Listener by call setRemoteConfigsListener(this)")
        }
        return configListener?.getAdWidgetData(this, key) ?: model
    }


    fun setListener(
        listener: SdkListener,
        testModeEnable: Boolean
    ) {
        sdkListener = listener
        setTestModeEnabled(testModeEnable)
    }

    fun setDialogListener(
        listener: SdkDialogsListener?,
    ) {
        sdkDialogsListener = listener
    }

    fun getSdkDialogListener(): SdkDialogsListener? {
        return sdkDialogsListener
    }

    fun getListener(): SdkListener? {
        if (sdkListener == null) {
            throw IllegalArgumentException("Please attach sdk listeners like SdkConfigs.setListener(this)")
        }
        return sdkListener
    }

    fun canShowAds(adKey: String, placementKey: String, adType: AdType): Boolean {
        if (sdkListener == null) {
            throw IllegalArgumentException("Please attach sdk listeners like SdkConfigs.setListener(this)")
        } else if (disableAllAds) {
            logAds("All Ads Are Disabled by developer", true)
            return false
        } else if (isAdManuallyBlockedForShow(adKey = adKey, adType = adType)) {
            return false
        } else {
            return sdkListener?.canShowAd(
                adType = adType,
                placementKey = placementKey,
                adKey = adKey
            ) ?: false
        }
    }

    fun canLoadAds(adKey: String, placementKey: String, adType: AdType): Boolean {
        if (sdkListener == null) {
            throw IllegalArgumentException("Please attach sdk listeners like SdkConfigs.setListener(this)")
        } else if (disableAllAds) {
            logAds("All Ads Are Disabled by developer", true)
            return false
        } else if (isAdManuallyBlockedForLoad(adKey, adType)) {
            return false
        } else {
            return sdkListener?.canLoadAd(
                adType = adType,
                placementKey = placementKey,
                adKey = adKey
            ) ?: false
        }
    }
}