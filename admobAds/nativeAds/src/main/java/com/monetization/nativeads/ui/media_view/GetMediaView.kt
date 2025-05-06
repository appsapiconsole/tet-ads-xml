package com.monetization.nativeads.ui.media_view

import android.content.Context
import android.widget.RelativeLayout.LayoutParams
import android.widget.RelativeLayout.generateViewId
import com.google.android.gms.ads.nativead.MediaView

interface GetMediaView {
    fun getNativeMediaView(context: Context): MediaView
}

class GetMediaViewImpl : GetMediaView {
    override fun getNativeMediaView(context: Context): MediaView {
        val mediaView = MediaView(context).apply {
            id = generateViewId()
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }
        return mediaView
    }
}