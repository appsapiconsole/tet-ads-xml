package com.monetization.core.listeners

sealed class AdConfigOption {
    data class UseConfigProvider(val provider: RemoteConfigsProvider) : AdConfigOption()
    data class UseJsonString(val enabled: Boolean, val jsonString: String) : AdConfigOption()
}