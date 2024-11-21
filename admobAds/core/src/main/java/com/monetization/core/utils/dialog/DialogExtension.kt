package com.monetization.core.utils.dialog

import android.app.Activity
import com.monetization.core.R
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.listeners.SdkDialogsListener


fun SdkDialogs.onAdLoadingDialogStateChange(
    showDialog: Boolean,
    isForBlackBg: Boolean,
) {
    if (showDialog) {
        if (isForBlackBg) {
            showBlackBgDialog()
        } else {
            showNormalLoadingDialog()
        }
    } else {
        hideLoadingDialog()
    }
}

fun SdkDialogs.showNormalLoadingDialog(
    color: Int = R.color.white
) {
    showLoadingDialog(
        showProgress = true,
        progressColor = color,
        textColor = color,
        bgColor = android.R.color.transparent
    )
}

fun SdkDialogs.showBlackBgDialog(bgColor: Int = R.color.black) {
    showLoadingDialog(
        message = "",
        showProgress = false,
        bgColor = bgColor
    )
}