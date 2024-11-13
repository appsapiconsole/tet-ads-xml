package com.monetization.core.counters

data class CounterInfo(
    val maxPoint: Int,
    val currentPoint: Int = 0,
    val adNotShownStrategy: CounterStrategies = CounterStrategies.ResetToZero,
    val adShownStrategy: CounterStrategies = CounterStrategies.ResetToZero,
) {
    fun isCounterReached() = currentPoint >= maxPoint
}

sealed class CounterStrategies {
    data object KeepSameValue : CounterStrategies()
    data object ResetToZero : CounterStrategies()
    data object HalfValue : CounterStrategies()
    data class SetStartingTo(val startPoint: Int) : CounterStrategies()
}