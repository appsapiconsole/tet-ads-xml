package com.monetization.core.provider.nativeAds

import com.google.android.gms.ads.nativead.NativeAdOptions

interface NativeOptionsProvider {
    fun getNativeAdOptions(): NativeAdOptions
}