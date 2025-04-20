package com.monetization.core.managers

import com.monetization.core.ad_units.core.AdType
import com.monetization.core.controllers.AdsControllerBaseHelper
import com.monetization.core.history.AdsManagerHistoryHelper
import com.monetization.core.managers.ControllersManager.adControllers


abstract class AdmobBaseAdsManager<T : AdsControllerBaseHelper>(adType: AdType) : AdsManager<T> {
    companion object {
    }

    private val adsMap = HashMap<String, T>()

    override fun isControllerRegistered(key: String): Boolean {
        return adsMap[key] != null
    }

    override fun getAdController(key: String): T? {
        return adsMap[key]
    }

    override fun updateIds(key: String, list: List<String>) {
        if (adsMap.containsKey(key)) {
            val controller = adsMap[key]
            controller?.updateAdIds(list)
        }
    }

    override fun addNewController(controller: T, replace: Boolean) {
        val key = controller.getAdKey()
        if (adsMap[key] == null || replace) {
            adsMap[key] = controller
            adControllers[key] = controller
            AdsManagerHistoryHelper.addController(controller)
        }
    }

    override fun removeController(adKey: String) {
        if (adsMap[adKey] != null) {
            adControllers.remove(adKey)
            adsMap.remove(adKey)
        }
    }

    override fun getAllController(): List<T> {
        return adsMap.values.toList()
    }
}