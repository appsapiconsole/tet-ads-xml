package com.monetization.core.models

data class LogInfo(
    val log: String,
    val tag: String,
    val isError: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis()
)
