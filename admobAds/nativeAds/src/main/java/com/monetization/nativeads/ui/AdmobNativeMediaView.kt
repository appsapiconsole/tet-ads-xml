package com.monetization.nativeads.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.gms.ads.nativead.MediaView

class AdmobNativeMediaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mediaView: MediaView? = null

    init {
        initializeMediaView()
    }

    private fun initializeMediaView() {
        if (mediaView == null) {
            mediaView = MediaView(context).apply {
                id = generateViewId()
            }
            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            super.addView(mediaView, 0, params)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child == null) return
        if (mediaView == null) {
            initializeMediaView()
        }
    }

    fun getMediaView(): MediaView? {
        return mediaView
    }
}
