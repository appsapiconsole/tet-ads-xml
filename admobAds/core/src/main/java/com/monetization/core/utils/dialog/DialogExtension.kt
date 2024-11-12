package com.monetization.core.utils.dialog

import com.monetization.core.R


fun SdkDialogs.showNormalLoadingDialog(
    color: Int = R.color.teal_700
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