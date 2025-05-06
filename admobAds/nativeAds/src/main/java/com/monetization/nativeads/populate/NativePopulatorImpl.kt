package com.monetization.nativeads.populate

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.monetization.core.commons.AdsCommons
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.NativeConstants.makeGone
import com.monetization.core.commons.Utils
import com.monetization.core.ui.AdsWidgetData
import com.monetization.nativeads.R
import com.monetization.nativeads.extensions.getDrawableOfRange

class NativePopulatorImpl : NativePopulator {
    override fun populateAd(
        activity: Activity,
        nativeAd: NativeAd?,
        adViewLayout: View?,
        adsWidgetData: AdsWidgetData?,
        addViewInParent: (View) -> Unit,
        onPopulated: () -> Unit
    ) {


        val admobNativeView: NativeAdView? = adViewLayout?.findViewById(R.id.adView)
        logAds(
            "populateNativeAd(${activity.localClassName.substringAfterLast(".")}) " + ",A," + "Native View Ok=${admobNativeView != null}"
        )

        adViewLayout?.parent?.let { parent ->
            (parent as ViewGroup).removeView(adViewLayout)
        }
        adViewLayout?.let { addViewInParent.invoke(it) }
        logAds(
            "populateNativeAd Called NativeAdsManager " +
                    ",isNativeAd=${nativeAd}"
        )
        nativeAd?.let {
            adViewLayout?.apply {
                admobNativeView?.let {
                    val adHeadLine: TextView? = findViewById(R.id.ad_headline)
                    logAds("populateAd adHeadLine:$adHeadLine ")
                    val adBody: TextView? = findViewById(R.id.ad_body)
                    val adCtaBtn: TextView? = findViewById(R.id.ad_call_to_action)
                    val mMedia: MediaView? = findViewById(R.id.ad_media)
                    val addAttrTextView: TextView? = findViewById(R.id.addAttr)
                    val mIconView = findViewById<ImageView>(R.id.ad_app_icon)
// Setting Up Ads Widget Data
                    setAdsWidgetData(
                        context = activity,
                        adsWidgetData = adsWidgetData,
                        adHeadLine = adHeadLine,
                        adBody = adBody,
                        adCtaBtn = adCtaBtn,
                        mIconView = mIconView,
                        attrTextView = addAttrTextView,
                        mediaView = mMedia
                    )
//
                    AdsCommons.logAds(
                        "populateAd isMediaViewOk=${mMedia != null}",
                        isError = mMedia == null
                    )
                    mMedia?.let {
                        admobNativeView.mediaView = mMedia
                        try {
                            admobNativeView.mediaView?.let { adMedia ->
                                adMedia.makeGone(nativeAd.mediaContent == null)
                                mMedia.makeGone(nativeAd.mediaContent == null)
                                if (nativeAd.mediaContent != null) {
                                    adMedia.mediaContent = nativeAd.mediaContent
                                }
                            } ?: run {
                                admobNativeView.mediaView?.makeGone()
                                mMedia.makeGone()
                            }
                        } catch (_: Exception) {
                            admobNativeView.mediaView?.makeGone()
                            mMedia.makeGone()
                        }
                    }
                    admobNativeView.iconView = mIconView
                    admobNativeView.iconView?.let {
                        nativeAd.icon.let { icon ->
                            admobNativeView.mediaView?.makeGone(icon == null)
                            if (icon != null) {
                                (it as? ImageView)?.setImageDrawable(icon.drawable)
                            }
                        }
                    } ?: run {
                        mIconView.makeGone()
                    }

                    admobNativeView.callToActionView = adCtaBtn
                    admobNativeView.bodyView = adBody
                    admobNativeView.headlineView = adHeadLine

                    if (nativeAd.headline.isNullOrEmpty()) {
                        adHeadLine?.visibility = View.GONE
                    } else {
                        adHeadLine?.visibility = View.VISIBLE
                        adHeadLine?.text = nativeAd.headline
                    }

                    if (nativeAd.body.isNullOrEmpty()) {
                        adBody?.visibility = View.GONE
                    } else {
                        adBody?.visibility = View.VISIBLE
                        adBody?.text = nativeAd.body
                    }
                    nativeAd.callToAction?.let { btn ->
                        adCtaBtn?.text = btn
                    }
                    admobNativeView.setNativeAd(nativeAd)
                    onPopulated.invoke()
                }
            }
        }
    }

    private fun setAdsWidgetData(
        context: Context,
        adsWidgetData: AdsWidgetData?,
        adHeadLine: TextView?,
        adBody: TextView?,
        adCtaBtn: TextView?,
        mediaView: MediaView?,
        mIconView: ImageView?,
        attrTextView: TextView?,
    ) {
        logAds("setAdsWidgetData=$adsWidgetData")
        adsWidgetData?.let { data ->
//          Cta Button
            adCtaBtn?.let { cta ->
                data.ctaRoundness?.let {
                    adCtaBtn.setBackgroundResource(it.getDrawableOfRange())
                }
                data.adCtaBgColor?.let {
                    Utils.setColorFilterByColor(
                        drawable = cta.background,
                        color = it.toColorInt()
                    )
                }
                setTextColor(cta, data.adCtaTextColor)
            }
            attrTextView?.let {
                data.adAttrBgColor?.let { attrBg ->
                    Utils.setColorFilterByColor(it.background, attrBg.toColorInt())
                }
            }

            val margins = adsWidgetData.margings ?: ""
            val marginList = margins.split(",")
            if (margins.isNotBlank() && marginList.size == 4) {
                val layoutParams = mediaView?.layoutParams as? ViewGroup.MarginLayoutParams
                layoutParams?.setMargins(
                    marginList[0].toIntOrZero(), // Left
                    marginList[1].toIntOrZero(), // Top
                    marginList[2].toIntOrZero(), // Right
                    marginList[3].toIntOrZero() // Bottom
                )
                mediaView?.layoutParams = layoutParams
            }



            setTextColor(view = adHeadLine, color = data.adHeadLineTextColor)
            setTextColor(view = adBody, color = data.adBodyTextColor)
            setTextColor(view = attrTextView, color = data.adAttrTextColor)
            setHeightWidthOfView(view = adCtaBtn, context = context, height = data.adCtaHeight)
            setHeightWidthOfView(mediaView, context, data.adMediaViewHeight)
            setHeightWidthOfView(mIconView, context, data.adIconHeight, data.adIconWidth)
            setTextSize(view = adHeadLine, textSize = data.adHeadlineTextSize)
            setTextSize(view = adBody, textSize = data.adBodyTextSize)
            setTextSize(view = adCtaBtn, textSize = data.adCtaTextSize)
        }
    }

    private fun setHeightWidthOfView(
        view: View?,
        context: Context,
        height: Float?,
        width: Float? = null,
    ) {
        view?.let {
            val layoutParams = view.layoutParams
            height?.let {
                layoutParams.height = dpToPx(context, height).toInt()
            }
            width?.let {
                layoutParams.width = dpToPx(context, width).toInt()
            }
            view.layoutParams = layoutParams
        }
    }

    private fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    private fun setTextSize(view: TextView?, textSize: Float? = null) {
        try {
            if (view is TextView) {
                textSize?.let {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                }
            }
        } catch (_: Exception) {
            logAds("Exception while setting text size on native", true)
        }
    }

    private fun setTextColor(view: TextView?, color: String? = null) {
        try {
            view?.let {
                color?.let {
                    view.setTextColor(it.toColorInt())
                }
            }
        } catch (_: Exception) {
            logAds("Exception while setting text color on native", true)
        }
    }

    private fun String.toIntOrZero(value: Int = 0): Int {
        return toIntOrNull() ?: value
    }

}