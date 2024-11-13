package com.monetization.adsmain.showRates.models

import com.monetization.adsmain.showRates.inter.showRatesHelper
import com.monetization.core.ad_units.core.AdType

sealed class IgnoreNewRequest(
    val afterLoadingEffects: IgnoreAfterEffects = IgnoreAfterEffects.DontIgnoreBeNormal
) {
    data object DontIgnore :
        IgnoreNewRequest(afterLoadingEffects = IgnoreAfterEffects.DontIgnoreBeNormal)

    data class IfTheseRequestingOrLoaded(
        val ids: List<String>,
        val afterEffects: IgnoreAfterEffects = IgnoreAfterEffects.DontIgnoreBeNormal
    ) :
        IgnoreNewRequest(afterLoadingEffects = afterEffects)

    data class IfTheseRequesting(
        val ids: List<String>,
        val afterEffects: IgnoreAfterEffects = IgnoreAfterEffects.DontIgnoreBeNormal
    ) : IgnoreNewRequest(afterLoadingEffects = afterEffects)

    data class IfTheseAvailable(
        val ids: List<String>,
        val afterEffects: IgnoreAfterEffects = IgnoreAfterEffects.DontIgnoreBeNormal
    ) : IgnoreNewRequest(afterLoadingEffects = afterEffects)
}

enum class IgnoreAfterEffects {
    IgnoreRequestNewIfAdShown,
    IgnoreRequestNewIfNotAvailable,
    IgnoreBothCalls,
    DontIgnoreBeNormal,
}

fun ignoreIfAnyOtherRequestingOrLoadedAd(
    adType: AdType,
    ignoreList: List<String> = listOf()
): IgnoreNewRequest {
    return IgnoreNewRequest.IfTheseRequestingOrLoaded(
        showRatesHelper.getLoadedOrRequestingAdsControllersByType(adType).map {
            it.getAdKey()
        }.filter {
            ignoreList.contains(it).not()
        }
    )
}

fun ignoreIfAnyOtherLoadedAd(
    adType: AdType,
    ignoreList: List<String> = listOf()
): IgnoreNewRequest {
    return IgnoreNewRequest.IfTheseRequestingOrLoaded(
        showRatesHelper.getLoadedAdsControllersByType(adType).map {
            it.getAdKey()
        }.filter {
            ignoreList.contains(it).not()
        }
    )
}
