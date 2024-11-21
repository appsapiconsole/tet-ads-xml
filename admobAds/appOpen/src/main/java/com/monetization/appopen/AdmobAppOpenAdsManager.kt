package com.monetization.appopen

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monetization.core.managers.AdmobBaseAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.TestAds

object AdmobAppOpenAdsManager : AdmobBaseAdsManager<AdmobAppOpenAdsController>(AdType.AppOpen),
    DefaultLifecycleObserver {

    init {
        addNewController(AdmobAppOpenAdsController("Test", listOf(TestAds.TestAppOpenId)))
    }


    private var listener: AppOpenListener? = null

    fun initAppOpen(callBack: AppOpenListener) {
        listener = callBack
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        listener?.onAppStart()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        listener?.onAppStop()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        listener?.onAppCreate()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        listener?.onAppDestroy()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        listener?.onAppPause()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        listener?.onAppResume()
    }

}