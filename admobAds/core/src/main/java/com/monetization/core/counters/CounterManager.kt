package com.monetization.core.counters

import android.util.Log
import com.monetization.core.counters.CounterManager.decrementCounter
import com.monetization.core.msgs.MessagesType

object CounterManager {

    private val counters = HashMap<String, CounterInfo>()
    private var safeMode = false

    fun createACounter(key: String, info: CounterInfo) {
        counters[key] = info
    }

    fun String.isCounterRegistered() = counters[this] != null

    fun adShownCounterReact(counterKey: String?, adShown: Boolean) {
        if (counterKey == null) {
            return
        }
        val model = counterKey.getCounterModel()
        if (model == null) {
            logCounterDetails("Please Register Counter Key !!!!", true)
            return
        }
        val strategy = if (adShown) {
            model.adShownStrategy
        } else {
            model.adNotShownStrategy
        }
        when (strategy) {
            CounterStrategies.KeepSameValue -> {
            }

            CounterStrategies.ResetToZero -> {
                counterKey.changeCurrentCounter(0)
            }

            is CounterStrategies.SetStartingTo -> {
                counterKey.changeCurrentCounter(strategy.startPoint)
            }

            CounterStrategies.HalfValue -> {
                val max = model.maxPoint
                if (max != 0) {
                    counterKey.changeCurrentCounter(max / 2)
                } else {
                    counterKey.changeCurrentCounter(0)
                }
            }
        }
    }

    fun counterWrapper(
        counterEnable: Boolean,
        key: String?,
        onCounterUpdate: ((Int) -> Unit)? = null,
        onDismiss: (Boolean, MessagesType?) -> Unit,
        showAd: () -> Unit
    ) {
        if (counterEnable.not() || key == null) {
            showAd.invoke()
            return
        }
        val model = key.getCounterModel()
        if (model == null) {
            logCounterDetails("Please Register Counter Key !!!!", true)
            onDismiss.invoke(false, MessagesType.CounterNotRegistered)
            return
        }
        val counterReached = model.isCounterReached()
        if (counterReached) {
            logCounterDetails("Counter Reached, model=$model")
            onCounterUpdate?.invoke(model.currentPoint)
            showAd.invoke()
        } else {
            key.incrementCounter()
            logCounterDetails("Counter Progress: Current=${key.getCounterModel()?.currentPoint},Target=${model.maxPoint}")
            onCounterUpdate?.invoke(model.currentPoint)
            onDismiss.invoke(false, MessagesType.CounterNotReached)
        }
    }

    fun setSafeMode(check: Boolean = true) {
        safeMode = check
    }

    fun String.isCounterReached(): Boolean {
        return getCounterModel()?.isCounterReached() == true
    }

    fun String.incrementCounter() {
        getCounterModel()?.let { model ->
            counters[this] = model.copy(
                currentPoint = model.currentPoint + 1
            )
        }
    }

    fun String.decrementCounter() {
        getCounterModel()?.let { model ->
            counters[this] = model.copy(
                currentPoint = model.currentPoint - 1
            )
        }
    }

    fun String.completeCounter() {
        getCounterModel()?.let { model ->
            counters[this] = model.copy(
                currentPoint = model.maxPoint
            )
        }
    }

    fun String.changeMaxCounter(maxCounter: Int) {
        getCounterModel()?.copy(
            maxPoint = maxCounter
        )?.let { model ->
            counters[this] = model
        }
    }

    fun String.changeCurrentCounter(counter: Int) {
        getCounterModel()?.copy(
            currentPoint = counter
        )?.let { model ->
            counters[this] = model
        }
    }

    fun String.getCounterModel(): CounterInfo? {
        val model = if (safeMode) {
            counters[this]
        } else {
            counters[this] ?: throw IllegalArgumentException("No Counter found against key : $this")
        }
        return model
    }

    fun logCounterDetails(msg: String, error: Boolean = false) {
        if (error) {
            Log.e("CounterLogs", "CounterLogs:$msg")
        } else {
            Log.d("CounterLogs", "CounterLogs:$msg")
        }
    }

}