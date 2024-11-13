package com.monetization.core.models

data class RefreshAdInfo(
    val showShimmer: Boolean = false,
    val hideAdOnFailure: Boolean = false,
    val requestNewIfAlreadyFailed: Boolean = true,
)
