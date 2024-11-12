package com.monetization.core.ui

import android.view.View

sealed class ShimmerInfo(
    open val hideShimmerOnFailure: Boolean = true,
) {

    data object None : ShimmerInfo()
    data class GivenLayout(
        val shimmerColor: String? = "#E0E0E0",
        val idsToChangeColor: List<Int> = listOf(),
        override val hideShimmerOnFailure: Boolean = true
    ) : ShimmerInfo()

    data class ShimmerByView(
        val layoutView: View?,
        val addInAShimmerView: Boolean = true,
        override val hideShimmerOnFailure: Boolean = true,
    ) : ShimmerInfo()
}