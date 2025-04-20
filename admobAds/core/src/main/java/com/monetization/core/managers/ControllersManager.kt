package com.monetization.core.managers

import com.monetization.core.controllers.AdsController

object ControllersManager {

    internal val adControllers = HashMap<String, AdsController>()
    fun getAllControllers(): List<AdsController> = adControllers.map { it.value }
}