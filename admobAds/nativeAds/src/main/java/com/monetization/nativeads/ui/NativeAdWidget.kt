package com.monetization.nativeads.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.facebook.shimmer.ShimmerFrameLayout
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.AdsCommons.adEnabledSdkString
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.NativeConstants.inflateLayoutByLayoutInfo
import com.monetization.core.commons.NativeConstants.removeViewsFromIt
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.models.RefreshAdInfo
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo
import com.monetization.core.ui.widgetBase.BaseAdsWidget
import com.monetization.nativeads.AdmobNativeAd
import com.monetization.nativeads.AdmobNativeAdsController
import com.monetization.nativeads.AdmobNativeAdsManager
import com.monetization.nativeads.R
import kotlinx.coroutines.launch

class NativeAdWidget @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseAdsWidget<AdmobNativeAdsController>(context, attrs, defStyleAttr) {

    init {
        logAds("NativeWidget called", true)
    }

    private var layoutView: LayoutInfo? = null


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
        val listener = getAdsLoadingListener()
        if (showFromHistory && adsController?.getHistory()?.isNotEmpty() == true) {
            listener.onAdLoaded(key)
        } else {
            (adsController as? AdmobNativeAdsController)?.loadAd(
                placementKey = adEnabledSdkString,
                activity = (activity!!),
                calledFrom = "Base Native Activity",
                callback = listener
            )
        }
    }


    fun showNativeAd(view: LayoutInfo, onShown: () -> Unit) {
        adUnit?.let {
            val layout = view.inflateLayoutByLayoutInfo(activity!!)
            val admobNativeView = layout.findViewById<AdmobNativeView>(R.id.adView)
            logAds(
                "populateNativeAd(${activity?.localClassName?.substringAfterLast(".")}) " +
                        ",Ad Ok=${adUnit != null}," +
                        "Layout Ok=${layout != null},Native View Ok=${admobNativeView != null}"
            )
            removeAllViews()
            layout.parent?.let { parent ->
                (parent as ViewGroup).removeView(layout)
            }

            addView(layout)
            admobNativeView?.let { view ->
                (it as AdmobNativeAd).populateAd(activity!!, view, adsWidgetData) {
                    refreshLayout()
                    onShown.invoke()
                }
            }
        }
    }

    override fun populateAd() {
        layoutView?.let {
            /*it.inflateLayoutByLayoutInfo(activity!!)?.let {
                it.parent?.let { parent ->
                    (parent as ViewGroup).removeView(it)
                }
                addView(it)
            }*/
            showNativeAd(view = it, onShown = {
                if (oneTimeUse) {
                    adsController?.destroyAd(activity!!)
                    if (requestNewOnShow) {
                        adsController?.loadAd(
                            placementKey = adEnabledSdkString,
                            activity = activity!!,
                            calledFrom = "requestNewOnShow",
                            callback = null
                        )
                    }
                }
            })
        }
    }

    override fun showShimmerLayout() {
        try {
            val info = shimmerInfo
            val shimmerLayout = LayoutInflater.from(activity)
                .inflate(com.monetization.core.R.layout.shimmer, null, false)
                ?.findViewById<ShimmerFrameLayout>(com.monetization.core.R.id.shimmerRoot)
            val shimmerView: View? = when (info) {
                is ShimmerInfo.GivenLayout -> {
                    val adLayout = layoutView?.inflateLayoutByLayoutInfo(activity!!)
                    if (info.shimmerColor != null) {
                        (listOf(
                            adLayout?.findViewById<View?>(R.id.ad_headline),
                            adLayout?.findViewById<View?>(R.id.ad_body),
                            adLayout?.findViewById<View?>(R.id.tv_ad),
                            adLayout?.findViewById<View?>(R.id.ad_app_icon),
                            adLayout?.findViewById<View?>(R.id.ad_media),
                            adLayout?.findViewById<View?>(R.id.ad_call_to_action),
                        ) + (info.idsToChangeColor).mapNotNull {
                            adLayout?.findViewById(it)
                        }).forEach {
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
                    shimmerLayout?.addView(adLayout)
                    shimmerLayout
                }

                is ShimmerInfo.ShimmerByView -> {
                    if (info.addInAShimmerView) {
                        info.layoutView?.let { view ->
                            try {
                                (view.parent as? ViewGroup)?.removeView(view)
                                shimmerLayout?.removeViewsFromIt()
                                shimmerLayout?.addView(view)
                                shimmerLayout
                            } catch (e: Exception) {
                                null
                            }
                        } ?: run { null }
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
                    addView(shimmerView)
                } catch (_: Exception) {

                }
            }
        } catch (e: Exception) {
            logAds("showShimmerLayout Native=$key Exception=${e.stackTrace}", true)
        }
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