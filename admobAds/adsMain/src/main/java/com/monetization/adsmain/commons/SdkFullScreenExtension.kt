package com.monetization.adsmain.commons

import android.app.Activity
import com.monetization.adsmain.showRates.full_screen_ads.FullScreenAdsShowManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons
import com.monetization.core.msgs.MessagesType
import com.monetization.core.utils.dialog.SdkDialogs
import com.monetization.core.utils.dialog.showNormalLoadingDialog


fun Activity.showFullScreenAd(
    adKey: String,
    isInstant: Boolean = true,
    onDismiss: (Boolean) -> Unit
) {
    val adType = adKey.getAdTypeByKey()
        ?: throw IllegalArgumentException("No Controller is available against $adKey")
    val sdkDialog = SdkDialogs(this)
    FullScreenAdsShowManager.showFullScreenAd(
        placementKey = AdsCommons.adEnabledSdkString,
        key = adKey,
        adType = adType,
        isInstantAd = isInstant,
        onLoadingDialogStatusChange = {
            if (it) {
                sdkDialog.showNormalLoadingDialog()
            } else {
                sdkDialog.hideLoadingDialog()
            }
        },
        onAdDismiss = { shown: Boolean, msg: MessagesType? ->
            onDismiss.invoke(shown)
        }
    )
}

fun String.getAdTypeByKey(): AdType? {
    return if (isAdKeyRegistered(AdType.INTERSTITIAL)) {
        AdType.INTERSTITIAL
    } else if (isAdKeyRegistered(AdType.AppOpen)) {
        AdType.AppOpen
    } else if (isAdKeyRegistered(AdType.REWARDED)) {
        AdType.REWARDED
    } else if (isAdKeyRegistered(AdType.REWARDED_INTERSTITIAL)) {
        AdType.REWARDED_INTERSTITIAL
    } else if (isAdKeyRegistered(AdType.NATIVE)) {
        AdType.NATIVE
    } else if (isAdKeyRegistered(AdType.BANNER)) {
        AdType.BANNER
    } else {
        null
    }
}

fun String.isAdKeyRegistered(adType: AdType): Boolean {
    return adType.getAdController(this) != null
}