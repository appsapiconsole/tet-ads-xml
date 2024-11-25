package com.monetization.appopen

interface AppOpenAndLifecycleListener {
    fun onAppStart()
    fun onAppCreate()
    fun onAppStop()
    fun onAppResume()
    fun onAppDestroy()
    fun onAppPause()
    fun onAppStopped()
}