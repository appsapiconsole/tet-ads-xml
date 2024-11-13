package com.monetization.core.models

import com.monetization.core.ad_units.core.AdType

data class ManualBlockModel(
    val adKey: String,
    val blockForShow: Boolean,
    val blockForLoad: Boolean,
    val adType: AdType
)