package com.monetization.appopen

interface AppOpenListener {
    fun onAppStart()
    fun onAppCreate()
    fun onAppStop()
    fun onAppResume()
    fun onAppDestroy()
    fun onAppStopped()
}