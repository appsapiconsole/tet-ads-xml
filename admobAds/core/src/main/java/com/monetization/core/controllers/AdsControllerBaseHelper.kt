package com.monetization.core.controllers

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.ads.LoadAdError
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

    //    private var canRequestAd = true
    private var state: ControllerState = ControllerState.NoAdAvailable
    private var isAdEnabled = true
    private var indexOfId = 0
    private var placementKey: String = ""
    private var adRequestCount = 0

    private var controllerListener: HashMap<String, ControllersListener?> = hashMapOf()

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
        this.controllerListener[adKey] = listener
    }

    override fun setControllerListener(key: String, listener: ControllersListener?) {
        this.controllerListener[key] = listener
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
        state = ControllerState.AdRequesting
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
        loadingStateListener?.onAdRequested(adKey = adKey)

        controllerListener.forEach {
            it.value?.onAdRequested(
                adKey = adKey,
                adType = adType,
                placementKey = placementKey,
                dataMap = customDataMap
            )
        }
        logAds("$adType Ad Requested,Key=$adKey,Id=$latestAdIdRequested")
    }


    fun onAdShown() {
        controllerListener.forEach {
            it.value?.onAdShown(
                adKey = adKey,
                adType = adType,
                placementKey = placementKey,
                dataMap = customDataMap
            )
        }
        logAds("$adType Ad Shown,Key=$adKey")
    }

    fun onAdClick() {
        controllerListener.forEach {
            it.value?.onAdClicked(
                adKey = adKey,
                adType = adType,
                placementKey = placementKey,
                dataMap = customDataMap
            )
        }
        logAds("$adType Ad Clicked,Key=$adKey")
    }

    fun onImpression() {
        adInfo = adInfo?.copy(
            adImpressionTime = System.currentTimeMillis()
        )
        addInAdHistory()

        controllerListener.forEach {
            it.value?.onAdImpression(
                adKey = adKey,
                adType = adType,
                placementKey = placementKey,
                dataMap = customDataMap
            )
        }
        logAds("$adType Ad Impression,Key=$adKey")
    }

    private var mediationClassName: String? = null
    fun onLoaded(mediationClassName: String?) {
        state = ControllerState.AdLoaded
        this.mediationClassName = mediationClassName
        adInfo = adInfo?.copy(
            adFinalTime = Loaded(System.currentTimeMillis())
        )
        addInAdHistory()
        loadingStateListener?.onAdLoaded(adKey, mediationClassName)

        controllerListener.forEach {
            it.value?.onAdLoaded(
                adKey = adKey,
                adType = adType,
                placementKey = placementKey,
                dataMap = customDataMap,
                mediationClassName
            )
        }
        logAds("$adType Ad Loaded,Key=$adKey,id=$latestAdIdRequested,Listener=${loadingStateListener}")
    }

    fun onAdRevenue(
        value: Long,
        currencyCode: String,
        precisionType: Int,
        adSourceName: String?,
        adSourceId: String?,
        adSourceInstanceName: String?,
        adSourceInstanceId: String?,
        extras: Bundle?
    ) {
        controllerListener.forEach {
            it.value?.onAdRevenue(
                adKey = adKey,
                adType = adType,
                value = value,
                currencyCode = currencyCode,
                precisionType = precisionType,
                adSourceName = adSourceName,
                adSourceId = adSourceId,
                adSourceInstanceName = adSourceInstanceName,
                adSourceInstanceId = adSourceInstanceId,
                extras = extras
            )
        }
        logAds("$adType Ad Revenue(value=$value,currency=$currencyCode,precision=$precisionType),Key=$adKey,id=$latestAdIdRequested")
    }

    data class AdapterResponses(
        val message: String?,
        val code: Int?,
        val adSourceId: String,
        val adSourceName: String,
        val adSourceInstanceId: String,
        val adSourceInstanceName: String,
        val adapterClassName: String,
        val credentials: Bundle,
        val latencyMillis: Long,
    )

    fun onAdFailed(
        error: LoadAdError
    ) {
        state = ControllerState.NoAdAvailable
        adInfo = adInfo?.copy(
            adFinalTime = Failed(
                System.currentTimeMillis(), error.message, error.toString()
            )
        )
        addInAdHistory()
        val adapterResponses = error.responseInfo?.adapterResponses?.map {
            AdapterResponses(
                message = it.adError?.message,
                code = it.adError?.code,
                adSourceId = it.adSourceId,
                adSourceName = it.adSourceName,
                adSourceInstanceId = it.adSourceInstanceId,
                adSourceInstanceName = it.adSourceInstanceName,
                adapterClassName = it.adapterClassName,
                credentials = it.credentials,
                latencyMillis = it.latencyMillis,
            )
        }
        loadingStateListener?.onAdFailedToLoad(
            adKey = adKey,
            message = error.message,
            code = error.code,
            mediationClassName = error.responseInfo?.mediationAdapterClassName,
            adapterResponses = adapterResponses
        )
        logAds(
            "$adType Ad Failed To Load, msg=${error.message},code=$error, Key=$adKey,id=$latestAdIdRequested",
            true
        )
        controllerListener.forEach {
            it.value?.onAdFailed(
                adKey = adKey,
                adType = adType,
                message = error.message,
                error = error.code,
                placementKey = placementKey,
                dataMap = customDataMap
            )
        }
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
        this.placementKey = placementKey
        logAds("$adType loadAd function called,enabled=$isAdEnabled,requesting=${isAdRequesting()},isAdAvailable=${isAdAvailable()}")
        this.loadingStateListener = callback
        if (isAdEnabled.not() || placementKey.isRemoteAdEnabled(adKey, adType).not()) {
            loadingStateListener?.onAdFailedToLoad(
                adKey,
                "$adType Ad is not enabled",
                -1,
                null,
                null
            )
            return false
        }
        if (isAdRequesting()) {
            return false
        }
        if (isAdAvailable()) {
            loadingStateListener?.onAdLoaded(adKey, mediationClassName)
            return false
        }
        if (canLoadAd(placementKey).not()) {
            callback?.onAdFailedToLoad(
                adKey,
                "Ad Is Restricted To Load, key=$adKey,type=$adType",
                -1,
                null,
                null
            )
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

    override fun getMediationAdapterClassName(): String? {
        return mediationClassName
    }

    override fun getAdIdsList(): List<String> {
        return mAdIdsList
    }

    override fun isAdRequesting(): Boolean {
        return state == ControllerState.AdRequesting
    }

    override fun isAdAvailableOrRequesting(): Boolean {
        return isAdRequesting() || isAdAvailable()
    }

    override fun onDismissed() {
        listener?.onAdDismissed(
            adKey = adKey,
            adType = adType,
            placementKey = placementKey,
            dataMap = customDataMap
        )
    }

    override fun onFailToShow() {
        listener?.onFailToShow(
            adKey = adKey,
            adType = adType,
            placementKey = placementKey,
            dataMap = customDataMap
        )
    }

    fun setControllerState(state: ControllerState) {
        this.state = state
    }

    override fun isAdAvailable(): Boolean {
        return state == ControllerState.AdLoaded
    }

}