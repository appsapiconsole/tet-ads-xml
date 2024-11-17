package com.monetization.core.listeners

import android.app.Activity
import com.monetization.core.ad_units.core.AdType

interface SdkDialogsListener {
    fun onAdLoadingDialogStateChange(
        activity: Activity,
        placementKey: String,
        adKey: String,
        adType: AdType,
        showDialog: Boolean,
        isForBlackBg: Boolean,
    )
}