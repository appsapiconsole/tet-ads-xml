package com.monetization.core.provider.ad_request

import com.google.android.gms.ads.AdRequest

interface AdRequestProvider {
    fun getAdRequest(): AdRequest
}