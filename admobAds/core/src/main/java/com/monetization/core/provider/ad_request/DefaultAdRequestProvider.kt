package com.monetization.core.provider.ad_request

import com.google.android.gms.ads.AdRequest

class DefaultAdRequestProvider : AdRequestProvider {
    override fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }
}