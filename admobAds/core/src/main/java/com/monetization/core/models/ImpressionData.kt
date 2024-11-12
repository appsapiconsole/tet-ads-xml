package com.monetization.core.models

import com.monetization.core.ad_units.core.AdType

data class AdmobAdInfo(
    val requestCount: Int,
    val adRequestTime: Long,
    val adImpressionTime: Long?=null,
    val adFinalTime: FinalPosition? = null,
    val adKey: String,
    val adId: String,
    val adType: AdType
)

fun FinalPosition.getMillis(): Long {
    return if (this is Loaded) time else if (this is Failed) time else 0
}

data class Loaded(
    val time: Long
) : FinalPosition()

data class Failed(
    val time: Long,
    val error: String,
    val code: String
) : FinalPosition()

abstract class FinalPosition

