package com.monetization.nativeads.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.gms.ads.nativead.NativeAdView


class AdmobNativeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var nativeAdView: NativeAdView? = null



    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child == null) return

        if (nativeAdView == null) {
            nativeAdView = NativeAdView(context).apply {
                id = generateViewId()
            }
            super.addView(nativeAdView, index, params)
        }

        nativeAdView?.addView(child)
    }

    fun getNativeAdView(): NativeAdView? {
        return nativeAdView
    }
}