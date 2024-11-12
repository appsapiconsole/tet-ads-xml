package com.monetization.core.commons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.monetization.core.ui.LayoutInfo

object NativeTemplates {
    const val SmallNative = "small_native_ad"
    const val SplitNative = "native_split"
    const val LargeNative = "large_native_ad"
    const val LargeNativeMediaTop = "large_native_ad_top_mediaview"
    const val SmallNativeRightSideCta = "small_native_ad_right_cta"
}

object TestAds {
    const val TestNativeId = "ca-app-pub-3940256099942544/2247696110"
    const val TestBannerId = "ca-app-pub-3940256099942544/9214589741"
    const val TestAppOpenId = "ca-app-pub-3940256099942544/9257395921"
    const val TestInterId = "ca-app-pub-3940256099942544/1033173712"
    const val TestRewardedId = "ca-app-pub-3940256099942544/5224354917"
    const val TestRewardedInterId = "ca-app-pub-3940256099942544/5354046379"
}

object NativeConstants {
    fun Context.getLayoutId(layoutName: String): Int {
        return resources.getIdentifier(layoutName, "layout", packageName)
    }

    fun View.removeViewsFromIt() {
        try {
            parent?.let { parent ->
                (parent as? ViewGroup)?.removeAllViews()
            }
        } catch (_: Exception) {
        }
    }


    fun String.getAdLayout(context: Context): View {
        try {
            val resourceId = context.resources.getIdentifier(this, "layout", context.packageName)
            return LayoutInflater.from(context).inflate(resourceId, null, false)
        } catch (_: Exception) {
            val resourceId = context.resources.getIdentifier(
                this,
                NativeTemplates.SmallNative,
                context.packageName
            )
            return LayoutInflater.from(context).inflate(resourceId, null, false)
        }
    }


    fun LayoutInfo.inflateLayoutByLayoutInfo(context: Context): View {
        return when (this) {
            is LayoutInfo.LayoutByName -> {
                this.layoutName.inflateLayoutByName(context)
            }

            is LayoutInfo.LayoutByXmlView -> {
                this.layoutRes.inflateLayoutById(context)
            }
        }
    }

    private fun Int.inflateLayoutById(context: Context): View {
        return getAdLayoutByXmlReference(context)
    }

    private fun Int.getAdLayoutByXmlReference(context: Context): View {
        return LayoutInflater.from(context).inflate(this, null, false)
    }
    fun String.inflateLayoutByName(context: Context): View {
        return getAdLayout(context)
    }

    fun View?.makeGone(value: Boolean = true) {
        this?.visibility = if (value) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun View.makeVisible(value: Boolean = true) {
        visibility = if (value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}