package com.monetization.core.ui

import androidx.annotation.LayoutRes

sealed class LayoutInfo {
    data class LayoutByName(
        val layoutName: String
    ) : LayoutInfo()

    data class LayoutByXmlView(
        @LayoutRes val layoutRes: Int
    ) : LayoutInfo()
}