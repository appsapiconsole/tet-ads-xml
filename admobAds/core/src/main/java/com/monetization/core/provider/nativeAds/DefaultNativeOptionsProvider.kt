package com.monetization.core.provider.nativeAds

import com.google.android.gms.ads.nativead.NativeAdOptions

class DefaultNativeOptionsProvider : NativeOptionsProvider {
    override fun getNativeAdOptions(): NativeAdOptions {
        return NativeAdOptions.Builder().build()
    }
}