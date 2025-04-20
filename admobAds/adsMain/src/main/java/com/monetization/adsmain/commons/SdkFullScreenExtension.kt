package com.monetization.adsmain.commons

import com.monetization.core.ad_units.core.AdType


fun String.getAdTypeByKey(): AdType? {
    return getAdController()?.getAdType()
}