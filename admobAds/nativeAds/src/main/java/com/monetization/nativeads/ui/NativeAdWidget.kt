package com.monetization.nativeads.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAd
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.NativeConstants.inflateLayoutByLayoutInfo
import com.monetization.core.commons.NativeConstants.removeViewsFromIt
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.models.RefreshAdInfo
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo
import com.monetization.core.ui.widgetBase.BaseAdsWidget
import com.monetization.nativeads.AdmobNativeAd
import com.monetization.nativeads.AdmobNativeAdsController
import com.monetization.nativeads.AdmobNativeAdsManager
import com.monetization.nativeads.R
import com.monetization.nativeads.populate.NativePopulator
import com.monetization.nativeads.populate.NativePopulatorImpl

class NativeAdWidget @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseAdsWidget<AdmobNativeAdsController>(context, attrs, defStyleAttr) {

    init {
        logAds("NativeWidget called", true)
    }

    private var layoutView: LayoutInfo? = null
    private var isRepeatCheckingAllowed = true
    private var adPopulator: NativePopulator = NativePopulatorImpl()

    fun setNativePopulator(adPopulator: NativePopulator) {
        this.adPopulator = adPopulator
    }


    fun setRepeatCheckingAllowed(check: Boolean) {
        isRepeatCheckingAllowed = check
    }

    fun showNativeAdmob(
        activity: Activity,
        adKey: String,
        adLayout: LayoutInfo,
        enabled: Boolean,
        shimmerInfo: ShimmerInfo = ShimmerInfo.GivenLayout(),
        oneTimeUse: Boolean = true,
        requestNewOnShow: Boolean = true,
        showFromHistory: Boolean = false,
        listener: UiAdsListener? = null
    ) {
        this.layoutView = if (isValuesFromRemote) {
            if (adsWidgetData?.adLayout != null) {
                LayoutInfo.LayoutByName(adsWidgetData!!.adLayout!!)
            } else {
                adLayout
            }
        } else {
            adLayout
        }
        onShowAdCalled(
            adKey = adKey,
            activity = activity,
            oneTimeUse = oneTimeUse,
            requestNewOnShow = requestNewOnShow,
            enabled = enabled,
            shimmerInfo = shimmerInfo,
            adsManager = AdmobNativeAdsManager,
            adType = AdType.NATIVE,
            listener = listener,
            showFromHistory = showFromHistory
        )
        logAds("showNativeAd called($key),enabled=$enabled,layoutView=$layoutView")
    }

    override fun loadAd() {
        val listener: AdsLoadingStatusListener = getAdsLoadingListener()
        logAds("loadAd Called In Native Ad Widget placementKey=$placementKey,listener=${listener}")
        if (showFromHistory && getController()?.getHistory()?.isNotEmpty() == true) {
            listener.onAdLoaded(key, getController()?.getMediationAdapterClassName())
        } else {
            val available = getController()?.isAdAvailable() ?: false
            (getController() as? AdmobNativeAdsController)?.loadAd(
                placementKey = placementKey,
                activity = (activity!!),
                calledFrom = "Base Native Activity",
                callback = listener
            )
            if (isRepeatCheckingAllowed && available.not()) {
                repeatCheck(listener)
            }
        }
    }

    private fun repeatCheck(listener: AdsLoadingStatusListener?) {
        if (listener != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                val available = getController()?.isAdAvailable()
                if (available == true) {
                    listener.onAdLoaded(key, getController()?.getMediationAdapterClassName())
                } else {
                    repeatCheck(listener)
                }
            }, 1000)
        }
    }


    fun showNativeAd(view: LayoutInfo, onShown: () -> Unit) {
        adUnit?.let {
            val layout = view.inflateLayoutByLayoutInfo(activity!!)
//            layout.findViewById<>()
            adPopulator.populateAd(
                activity = activity!!,
                nativeAd = (it as? AdmobNativeAd)?.nativeAd,
                adViewLayout = layout,
                adsWidgetData = adsWidgetData,
                addViewInParent = {
                    addView(it)
                }
            ) {
                refreshLayout()
                onShown.invoke()
            }
            /*admobNativeView?.let { view ->
                (it as AdmobNativeAd).populateAd(activity!!, view, adsWidgetData) {
                    refreshLayout()
                    onShown.invoke()
                }
            }*/
        }
    }

    override fun populateAd() {
        layoutView?.let {
            showNativeAd(view = it, onShown = {
                if (oneTimeUse) {
                    getController()?.destroyAd(activity!!)
                    if (requestNewOnShow) {
                        getController()?.loadAd(
                            placementKey = placementKey,
                            activity = activity!!,
                            calledFrom = "requestNewOnShow",
                            callback = null
                        )
                    }
                }
                uiListener?.onAdPopulated(key)
            })
        }
    }

    override fun showShimmerLayout(shimmer: ShimmerInfo?, activity: Activity) {
//        try {
        val info: ShimmerInfo = shimmer ?: shimmerInfo
        val shimmerLayout = LayoutInflater.from(activity)
            .inflate(com.monetization.core.R.layout.shimmer, null, false)
            ?.findViewById<ShimmerFrameLayout>(com.monetization.core.R.id.shimmerRoot)
        val shimmerView: View? = when (info) {
            is ShimmerInfo.GivenLayout -> {
                val adLayout = layoutView?.inflateLayoutByLayoutInfo(activity)
                if (info.shimmerColor != null) {
                    val ids = (listOf(
                        R.id.ad_headline,
                        R.id.ad_body,
                        R.id.tv_ad,
                        R.id.ad_app_icon,
                        R.id.ad_media,
                        R.id.ad_call_to_action,
                    ) + info.idsToChangeColor).filter {
                        info.idsToExclude.contains(it).not()
                    }.map {
                        adLayout?.findViewById<View?>(it)
                    }
                    ids.forEach {
                        if (it is TextView) {
                            if (info.removeTextAdSpaced) {
                                var text = ""
                                repeat(it.text.length) {
                                    text += " "
                                }
                                it.text = text
                            }
                        }
                        it?.setBackgroundColor(
                            try {
                                Color.parseColor(info.shimmerColor)
                            } catch (_: Exception) {
                                logAds(
                                    "Bad Shimmer Color !!!!!!!!!!! : ${info.shimmerColor}", true
                                )
                                ContextCompat.getColor(context, R.color.shimmercolor)
                            }
                        )
                    }
                }
                shimmerLayout?.removeViewsFromIt()
                shimmerLayout?.addView(adLayout)
                shimmerLayout
            }

            is ShimmerInfo.ShimmerByView -> {
                if (info.addInAShimmerView) {
                    info.layoutView?.let { view ->
                        try {
                            (view.parent as? ViewGroup)?.removeView(view)
                            if (info.shimmerColor.isNullOrBlank().not()) {
                                val ids = (listOf(
                                    R.id.ad_headline,
                                    R.id.ad_body,
                                    R.id.tv_ad,
                                    R.id.ad_app_icon,
                                    R.id.ad_media,
                                    R.id.ad_call_to_action,
                                ) + info.idsToChangeColor).filter {
                                    info.idsToExclude.contains(it).not()
                                }.mapNotNull {
                                    view.findViewById<View?>(it)
                                }
                                ids.forEach {
                                    if (it is TextView) {
                                        if (info.removeTextAdSpaced) {
                                            var text = ""
                                            repeat(it.text.length) {
                                                text += " "
                                            }
                                            it.text = text
                                        }
                                    }
                                    it?.setBackgroundColor(
                                        try {
                                            Color.parseColor(info.shimmerColor)
                                        } catch (_: Exception) {
                                            logAds(
                                                "Bad Shimmer Color !!!!!!!!!!! : ${info.shimmerColor}",
                                                true
                                            )
                                            ContextCompat.getColor(context, R.color.shimmercolor)
                                        }
                                    )
                                }
                            }

                            shimmerLayout?.removeViewsFromIt()
                            shimmerLayout?.addView(view)
                            shimmerLayout
                        } catch (e: Exception) {
                            logAds(
                                "ShimmerByView crashes when adding our view for shimmer in shimmer, ${e.message}",
                                true
                            )
                            null
                        }
                    } ?: run {
                        logAds("info.layoutView is null in show shimmer", true)
                        null
                    }
                } else {
                    info.layoutView
                }
            }

            ShimmerInfo.None -> {
                null
            }
        }
        removeAllViews()
        if (shimmerView != null) {
            try {
                logAds("Adding Shimmer view in widget")
                addView(shimmerView)
            } catch (e: Exception) {
                logAds("At final point when adding shimmer in widget it crashed\n${e.message}")
            }
        } else {
            logAds("At final point Provided shimmer view is null")
        }/*} catch (e: Exception) {
            logAds("showShimmerLayout Native=$key Exception=${e.message}", true)
        }*/
    }

    fun refreshAd(refreshAdInfo: RefreshAdInfo) {
        if (adPopulated || (refreshAdInfo.requestNewIfAlreadyFailed && isAdFailedToLoad)) {
            isAdFailedToLoad = false
            adPopulated = false
            isLoadAdCalled = false
            activity?.let {
                onShowAdCalled(
                    adKey = key,
                    activity = it,
                    oneTimeUse = oneTimeUse,
                    requestNewOnShow = requestNewOnShow,
                    enabled = isAdEnabled,
                    shimmerInfo = shimmerInfo,
                    adsManager = AdmobNativeAdsManager,
                    adType = AdType.NATIVE,
                    listener = null,
                    isForRefresh = true,
                    refreshAdInfo = refreshAdInfo
                )
            }
        }
    }

    fun setWidgetKey(key: String) {
        placementKey = key
    }

}