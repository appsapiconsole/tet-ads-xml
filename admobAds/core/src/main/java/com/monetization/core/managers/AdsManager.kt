package com.monetization.core.managers

import com.monetization.core.controllers.AdsController
import com.monetization.core.controllers.AdsControllerBaseHelper

interface AdsManager<T : AdsControllerBaseHelper> {
    fun getAdController(key: String): AdsController?
    fun addNewController(controller: T, replace: Boolean = false)
    fun updateIds(key: String, list: List<String>)
    fun removeController(adKey: String)
    fun getAllController(): List<T>
}