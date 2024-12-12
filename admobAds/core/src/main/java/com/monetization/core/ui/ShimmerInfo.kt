package com.monetization.core.ui

import android.view.View

sealed class ShimmerInfo(
    open val hideShimmerOnFailure: Boolean = true,
) {

    data object None : ShimmerInfo()
    data class GivenLayout(
        val shimmerColor: String? = "#E0E0E0",
        val idsToChangeColor: List<Int> = listOf(),
        val idsToExclude: List<Int> = listOf(),
        val removeTextAdSpaced: Boolean = true,
        override val hideShimmerOnFailure: Boolean = true
    ) : ShimmerInfo()

    data class ShimmerByView(
        val layoutView: View?,
        val shimmerColor: String? = "#E0E0E0",
        val removeTextAdSpaced: Boolean = true,
        val idsToChangeColor: List<Int> = listOf(),
        val idsToExclude: List<Int> = listOf(),
        val addInAShimmerView: Boolean = true,
        override val hideShimmerOnFailure: Boolean = true,
    ) : ShimmerInfo()
}