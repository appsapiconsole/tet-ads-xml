package com.example.adsxml

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.adsxml.databinding.ActivityMainBinding
import com.monetization.adsmain.commons.addNewController
import com.monetization.adsmain.commons.loadAd
import com.monetization.adsmain.commons.sdkBannerAd
import com.monetization.adsmain.commons.sdkNativeAd
import com.monetization.adsmain.commons.sdkNativeAdd
import com.monetization.adsmain.splash.AdmobSplashAdController
import com.monetization.adsmain.splash.SplashAdType
import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.NativeTemplates
import com.monetization.core.commons.Utils.resToView
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.msgs.MessagesType
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo
import com.monetization.core.utils.dialog.SdkDialogs
import com.monetization.core.utils.dialog.showNormalLoadingDialog
import com.monetization.interstitials.AdmobInterstitialAdsManager
import com.monetization.interstitials.extensions.InstantInterstitialAdsManager
import com.monetization.interstitials.extensions.counter.InstantCounterInterAdsManager
import com.monetization.nativeads.AdmobNativeAdsManager
import com.remote.firebaseconfigs.RemoteCommons.toConfigString
import com.remote.firebaseconfigs.SdkConfigListener
import com.remote.firebaseconfigs.SdkRemoteConfigController
import org.koin.android.ext.android.inject

private var TestEnabled = false

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private val splashAdController: AdmobSplashAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AdmobInterstitialAdsManager.addNewController(
            "Splash", listOf("")
        )

        splashAdController.showSplashAd(
            enableKey = true.toConfigString(),
            adType = SplashAdType.AdmobInter("Splash"),
            activity = this,
            lifecycle = lifecycle,
            callBack = object : FullScreenAdsShowListener {
                override fun onAdDismiss(adKey: String, adShown: Boolean, rewardEarned: Boolean) {
                    super.onAdDismiss(adKey, adShown, rewardEarned)
                    Toast.makeText(this@MainActivity, "Did it=$adShown", Toast.LENGTH_SHORT).show()
                }
            },
            timeInMillis = 15_000
        )


        /*

                AdmobInterstitialAdsManager.addNewController(
                    "Inter", listOf("")
                )
                AdmobNativeAdsManager.addNewController(
                    "Native", listOf("", "", "", "")
                )
                binding.preloadAd.setOnClickListener {
                    "Inter".loadAd(true.toConfigString(), this@MainActivity, adType = AdType.NATIVE)
                }
                binding.fetchConfig.setOnClickListener {
                    fetchRemoteConfigController {
                        showNativeAd()
                    }
                }
                binding.reloadAd.setOnClickListener {
                }
                val sdkDialogs = SdkDialogs(this@MainActivity)

                binding.refreshAd.setOnClickListener {
                    val view = LayoutInflater.from(this@MainActivity)
                        .inflate(
                            com.monetization.nativeads.R.layout.small_native_ad,
                            null, false
                        )
                    binding.adFrame.getNativeWidget().showNativeAd(
                        view = LayoutInfo.LayoutByView(view),
                        onShown = {

                        }
                    )
                }
                binding.showAd.setOnClickListener {
                    showNativeAd()
                }
        */

    }

    private fun showCounterAd(onAdDismiss: (Boolean, MessagesType?) -> Unit) {
        val sdkDialog = SdkDialogs(this@MainActivity)
        InstantCounterInterAdsManager.showInstantInterstitialAd(
            placementKey = true.toConfigString(),
            activity = this@MainActivity,
            key = "Inter",
            onAdDismiss = onAdDismiss,
            onLoadingDialogStatusChange = {
                if (it) {
                    sdkDialog.showNormalLoadingDialog()
                } else {
                    sdkDialog.hideLoadingDialog()
                }
            },
            counterKey = "MainScreen"
        )
    }

    private fun assignConfigs() {
        TestEnabled = SdkRemoteConfigController.getRemoteConfigBoolean("TestEnabled")
        Toast.makeText(this, "TestEnabled=$TestEnabled", Toast.LENGTH_SHORT).show()
    }

    private fun showNativeAd() {
        val view = R.layout.small_native_ad_main.resToView(this)
        val ctaBtn = view?.findViewById<AppCompatButton>(R.id.ad_call_to_action)
        ctaBtn?.setTextColor(ContextCompat.getColor(this, R.color.red))
        ctaBtn?.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        view?.let {
            binding.adFrame.sdkNativeAdd(
                adLayout = LayoutInfo.LayoutByName("small_native_ad_main"),
                adKey = "Native",
                placementKey = true.toConfigString(),
                showNewAdEveryTime = true,
                showShimmerLayout = ShimmerInfo.GivenLayout(),
                lifecycle = lifecycle,
                listener = object : UiAdsListener {
                    override fun onAdClicked(key: String) {
                        super.onAdClicked(key)
                    }
                },
                activity = this
            )
        }
    }

    fun showInterstitial(onAdDismiss: (Boolean) -> Unit) {
        InstantInterstitialAdsManager.showInstantInterstitialAd(
            placementKey = true.toConfigString(),
            key = "Inter",
            activity = this,
            onAdDismiss = { adShown, msg ->
                onAdDismiss.invoke(adShown)
            },
            onLoadingDialogStatusChange = {

            }
        )
    }

    private fun refreshAd() {
        binding.adFrame.refreshAd(isNativeAd = false)
    }

    private fun showBannerAd() {
        binding.adFrame.sdkBannerAd(activity = this,
            type = BannerAdType.Normal(BannerAdSize.AdaptiveBanner),
            adKey = "Banner",
            placementKey = true.toConfigString(),
            showNewAdEveryTime = true,
            showOnlyIfAdAvailable = true,
            lifecycle = lifecycle,
            listener = object : UiAdsListener {
                override fun onAdClicked(key: String) {
                    super.onAdClicked(key)
                }
            })
    }

    private fun fetchRemoteConfigController(done: (Boolean) -> Unit) {
        SdkRemoteConfigController.fetchRemoteConfig(
            R.xml.remote_defaults,
            callback = object : SdkConfigListener {
                override fun onDismiss() {

                }

                override fun onFailure(error: String) {
                    done.invoke(false)
                }

                override fun onSuccess() {
                    done.invoke(true)
                }

                override fun onUpdate() {

                }
            },
            fetchOutTimeInSeconds = 8L,
            onUpdate = {}
        )
    }
}