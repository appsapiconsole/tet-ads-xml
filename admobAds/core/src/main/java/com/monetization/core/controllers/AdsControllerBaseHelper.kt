package com.monetization.core.controllers

import android.app.Activity
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.ad_units.core.AdUnit
import com.monetization.core.commons.AdsCommons
import com.monetization.core.commons.AdsCommons.logAds
import com.monetization.core.commons.SdkConfigs
import com.monetization.core.commons.SdkConfigs.isRemoteAdEnabled
import com.monetization.core.history.AdsManagerHistoryHelper
import com.monetization.core.listeners.ControllersListener
import com.monetization.core.managers.AdsLoadingStatusListener
import com.monetization.core.models.AdmobAdInfo
import com.monetization.core.models.Failed
import com.monetization.core.models.Loaded

abstract class AdsControllerBaseHelper(
    private val adKey: String,
    private val adType: AdType,
    adIdsList: List<String>,
    private val listener: ControllersListener?,
) : AdsController {

    private var mAdIdsList = adIdsList

    init {
        if (mAdIdsList.isEmpty()) {
            throw IllegalArgumentException("Please Provide Ids For key=${adKey}, $adType,")
        }
    }

    private var adsHistory = mutableListOf<AdUnit>()
    private var canRequestAd = true
    private var isAdEnabled = true
    private var indexOfId = 0
    private var adRequestCount = 0

    private var controllerListener: ControllersListener? = null

    private var adInfo: AdmobAdInfo? = null
    private var latestAdIdRequested: String = ""

    private var customDataMap = HashMap<String, String>()

    private var loadingStateListener: AdsLoadingStatusListener? = null

    override fun saveInHistory(adUnit: AdUnit) {
        adsHistory.add(adUnit)
    }

    override fun getHistory(): List<AdUnit> {
        return adsHistory
    }

    override fun updateAdIds(list: List<String>) {
        mAdIdsList = list
    }

    override fun getAdId(): String {
        return try {
            mAdIdsList[indexOfId]
        } catch (_: Exception) {
            logAds("Exception In getAdId ", true)
            mAdIdsList[0]
        }
    }

    fun getAdIdAndIncrementIndex(): String {
        val current = indexOfId
        val adId = AdsCommons.getAdId(indexOfId, mAdIdsList, adType) {
        }
        if (indexOfId >= mAdIdsList.size - 1) {
            indexOfId = 0
        } else {
            indexOfId += 1
        }
        logAds("For Request(current=$current,next=$indexOfId):$adId")
        latestAdIdRequested = adId
        return adId
    }

    fun setDataMap(data: HashMap<String, String>) {
        customDataMap = data
    }

    init {
        this.controllerListener = listener
    }

    override fun setControllerListener(listener: ControllersListener?) {
        this.controllerListener = listener
    }


    private fun canLoadAd(placementKey: String): Boolean {
        val canLoadAd = SdkConfigs.canLoadAds(
            adKey = adKey,
            placementKey = placementKey,
            adType = adType
        )
        if (canLoadAd.not()) {
            logAds(
                "You Blocked Request Of $adType Ads,Key=$adKey",
                true
            )
        } else {
            logAds(
                "$adType Request Allowed of key=$adKey"
            )
        }
        return canLoadAd
    }


    fun onAdRequested(
    ) {
        canRequestAd = false
        adRequestCount += 1
        adInfo = AdmobAdInfo(
            adRequestTime = System.currentTimeMillis(),
            adKey = adKey,
            adType = adType,
            adId = latestAdIdRequested,
            requestCount = adRequestCount,
            adImpressionTime = null
        )
        addInAdHistory()
        loadingStateListener?.onAdRequested(adKey)
        controllerListener?.onAdRequested(adKey, adType, customDataMap)
        logAds("$adType Ad Requested,Key=$adKey,Id=$latestAdIdRequested")
    }

    fun onAdClick() {
        controllerListener?.onAdClicked(adKey, adType, customDataMap)
        logAds("$adType Ad Clicked,Key=$adKey")
    }

    fun onImpression() {
        adInfo = adInfo?.copy(
            adImpressionTime = System.currentTimeMillis()
        )
        addInAdHistory()
        controllerListener?.onAdImpression(adKey, adType, customDataMap)
        logAds("$adType Ad Impression,Key=$adKey")
    }

    fun onLoaded() {
        canRequestAd = true
        adInfo = adInfo?.copy(
            adFinalTime = Loaded(System.currentTimeMillis())
        )
        addInAdHistory()
        loadingStateListener?.onAdLoaded(adKey)
        controllerListener?.onAdLoaded(adKey, adType, customDataMap)
        logAds("$adType Ad Loaded,Key=$adKey,id=$latestAdIdRequested")
    }

    fun onAdRevenue(
        value: Long, currencyCode: String, precisionType: Int,
    ) {
        controllerListener?.onAdRevenue(adKey, adType, value, currencyCode, precisionType)
        logAds("$adType Ad Revenue(value=$value,currency=$currencyCode,precision=$precisionType),Key=$adKey,id=$latestAdIdRequested")
    }


    fun onAdFailed(
        message: String, error: Int,
    ) {
        canRequestAd = true
        adInfo = adInfo?.copy(
            adFinalTime = Failed(
                System.currentTimeMillis(), message, error.toString()
            )
        )
        addInAdHistory()
        loadingStateListener?.onAdFailedToLoad(adKey, message, error)
        controllerListener?.onAdFailed(adKey, adType, message, error, customDataMap)
        logAds(
            "$adType Ad Failed To Load, msg=$message,code=$error, Key=$adKey,id=$latestAdIdRequested",
            true
        )
    }

    private fun addInAdHistory() {
        adInfo?.let {
            AdsManagerHistoryHelper.addInHistory(it)
        }
    }

    fun commonLoadAdChecks(
        placementKey: String,
        callback: AdsLoadingStatusListener?,
    ): Boolean {

        logAds("$adType loadAd function called,enabled=$isAdEnabled,requesting=${isAdRequesting()},isAdAvailable=${isAdAvailable()}")
        this.loadingStateListener = callback
        if (isAdEnabled.not() || placementKey.isRemoteAdEnabled(adKey, adType).not()) {
            loadingStateListener?.onAdFailedToLoad(adKey, "${adType} Ad is not enabled", -1)
            return false
        }
        if (isAdRequesting()) {
            return false
        }
        if (isAdAvailable()) {
            loadingStateListener?.onAdLoaded(adKey)
            return false
        }
        if (canLoadAd(placementKey).not()) {
            callback?.onAdFailedToLoad(adKey, "Ad Is Restricted To Load, key=$adKey,type=$adType")
            return false
        }
        return true
    }

    override fun setAdEnabled(enabled: Boolean) {
        isAdEnabled = enabled
    }


    override fun resetListener(activity: Activity) {
        loadingStateListener = null
    }

    override fun setListener(activity: Activity, callback: AdsLoadingStatusListener) {
        loadingStateListener = callback
    }

    override fun getAdType(): AdType {
        return adType
    }

    override fun getAdKey(): String {
        return adKey
    }

    override fun getAdIdsList(): List<String> {
        return mAdIdsList
    }

    override fun isAdRequesting(): Boolean {
        return canRequestAd.not()
    }

    override fun isAdAvailableOrRequesting(): Boolean {
        return isAdRequesting() || isAdAvailable()
    }

    override fun onDismissed() {
        listener?.onAdDismissed(adKey, adType, dataMap = customDataMap)
    }

    override fun onFailToShow() {
        listener?.onFailToShow(adKey, adType, dataMap = customDataMap)
    }

}