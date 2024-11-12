package com.monetization.adsmain.sdk

import android.content.Context
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AdmobAdsSdk(
) {
    private var isSdkInitialized = false

    fun initAdsSdk(
        context: Context,
        initAdsSdk: Boolean = true,
        onInitialized: () -> Unit,
    ) {
        if (initAdsSdk) {
            initAdmobSdk(context, onInitialized)
        }
    }

    private fun initAdmobSdk(context: Context, onInitialized: () -> Unit) {
        if (isSdkInitialized) {
            return
        }
        isSdkInitialized = true
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(context) {
                CoroutineScope(Dispatchers.Main).launch {
                    onInitialized.invoke()
                }
            }
            MobileAds.setAppMuted(true)
        }
    }
}