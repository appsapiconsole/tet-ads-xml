package com.monetization.core.ui

import android.view.View
import androidx.annotation.LayoutRes

sealed class LayoutInfo {
    data class LayoutByName(
        val layoutName: String
    ) : LayoutInfo()

    data class LayoutByView(
        val view: View
    ) : LayoutInfo()

    data class LayoutByXmlView(
        @LayoutRes val layoutRes: Int
    ) : LayoutInfo()
}