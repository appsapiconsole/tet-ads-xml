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
        listener?.onShowAd()
    }

}