package com.monetization.core.showRates

object ShowRatesCommons {

    private var bestShowRatesEnabled = false
    private var bestShowRates = BestShowRates()

    fun setBestShowRates(bestShowRates: BestShowRates) {
        this.bestShowRates = bestShowRates
    }
    fun getBestShowRates(): BestShowRates {
        return bestShowRates
    }

    fun setRequestLimits(bestShowRates: BestShowRates) {
        this.bestShowRates = bestShowRates
    }

    fun setBestShowRatesEnabled(enabled: Boolean) {
        bestShowRatesEnabled = enabled
    }

    fun isBestShowRatesEnabled(): Boolean {
        return bestShowRatesEnabled
    }


}