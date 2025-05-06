/*
package com.monetization.nativeads.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.gms.ads.nativead.MediaView
import com.monetization.nativeads.ui.media_view.GetMediaView
import com.monetization.nativeads.ui.media_view.GetMediaViewImpl

class AdmobNativeMediaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mediaView: MediaView? = null

    private var getMediaView: GetMediaView = GetMediaViewImpl()

    fun setGetMediaView(getMediaView: GetMediaView) {
        this.getMediaView = getMediaView
    }

    init {
        initializeMediaView()
    }

    private fun initializeMediaView() {
        if (mediaView == null) {
            mediaView = getMediaView.getNativeMediaView(context)
            super.addView(mediaView, 0)
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
*/
