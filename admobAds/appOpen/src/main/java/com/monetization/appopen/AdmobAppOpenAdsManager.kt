package com.monetization.appopen

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.TestAds
import com.monetization.core.managers.AdmobBaseAdsManager

object AdmobAppOpenAdsManager : AdmobBaseAdsManager<AdmobAppOpenAdsController>(AdType.AppOpen),
    DefaultLifecycleObserver {

        val TAG = "AdmobAppOpenAdsManagerTag"

    init {
        addNewController(AdmobAppOpenAdsController("Test", listOf(TestAds.TestAppOpenId)))
    }


    private var listener: AppOpenAndLifecycleListener? = null

    fun initAppOpen(callBack: AppOpenAndLifecycleListener) {
        listener = callBack
        Log.d(TAG, "initAppOpen: ")
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        listener?.onAppStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        listener?.onAppStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        listener?.onAppCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        listener?.onAppDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        listener?.onAppPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        listener?.onAppResume()
        Log.d(TAG, "onResume: ")
    }

}