package com.monetization.core.commons

import com.monetization.core.commons.Utils.getLongSafe
import com.monetization.core.commons.Utils.getStringSafe
import com.monetization.core.ui.AdsWidgetData
import org.json.JSONObject

fun String.placementToAdWidgetModel(default: AdsWidgetData? = null): AdsWidgetData? {
    try {
        return JSONObject(this).run {
            val enabled = getLongSafe("enabled")?.toInt()
            if (enabled != null && enabled == 1) {
                AdsWidgetData(
                    enabled = enabled,
                    refreshTime = getLongSafe("refreshTime"),
                    margings = getStringSafe("margins"),
                    adCtaHeight = getLongSafe("adCtaHeight")?.toFloat(),
                    adCtaTextSize = getLongSafe("adCtaTextSize")?.toFloat(),
                    adMediaViewHeight = getLongSafe("adMediaViewHeight")?.toFloat(),
                    adIconHeight = getLongSafe("adIconHeight")?.toFloat(),
                    adIconWidth = getLongSafe("adIconWidth")?.toFloat(),
                    adHeadlineTextSize = getLongSafe("adHeadlineTextSize")?.toFloat(),
                    adBodyTextSize = getLongSafe("adBodyTextSize")?.toFloat(),
                    adCtaBgColor = getStringSafe("adCtaBgColor"),
                    ctaRoundness = getLongSafe("ctaRoundness")?.toInt(),
                    adLayout = getStringSafe("adLayout"),
                    adCtaTextColor = getStringSafe("adCtaTextColor"),
                    adHeadLineTextColor = getStringSafe("adHeadLineTextColor"),
                    adBodyTextColor = getStringSafe("adBodyTextColor"),
                    adAttrTextColor = getStringSafe("adAttrTextColor"),
                    adAttrBgColor = getStringSafe("adAttrBgColor"),
                )
            } else {
                default
            }
        }
    } catch (_: Exception) {
        return null
    }
}