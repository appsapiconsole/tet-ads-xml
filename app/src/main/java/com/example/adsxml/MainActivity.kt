package com.example.adsxml

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.adsxml.databinding.ActivityMainBinding
import com.monetization.adsmain.commons.addNewController
import com.monetization.adsmain.commons.loadAd
import com.monetization.adsmain.commons.loadAdDirectly
import com.monetization.adsmain.commons.sdkBannerAd
import com.monetization.adsmain.commons.sdkNativeAd
import com.monetization.adsmain.splash.AdmobSplashAdController
import com.monetization.bannerads.BannerAdSize
import com.monetization.bannerads.BannerAdType
import com.monetization.core.commons.NativeTemplates
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.msgs.MessagesType
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
            "Inter", listOf("")
        )
        AdmobNativeAdsManager.addNewController(
            "Native", listOf("", "", "", "")
        )
        binding.preloadAd.setOnClickListener {
            "Inter".loadAd(true.toConfigString())
        }
        binding.fetchConfig.setOnClickListener {
            fetchRemoteConfigController {
                showNativeAd()
            }
        }
        binding.reloadAd.setOnClickListener {
            "Native".loadAdDirectly()
        }
        val sdkDialogs = SdkDialogs(this@MainActivity)
        binding.showAd.setOnClickListener {
            showNativeAd()
        }

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
        binding.adFrame.sdkNativeAd(
            adLayout = NativeTemplates.LargeNative,
            adKey = "Native",
            placementKey = "Native",
            showNewAdEveryTime = true,
            lifecycle = lifecycle,
            listener = object : UiAdsListener {
                override fun onAdClicked(key: String) {
                    super.onAdClicked(key)
                }
            })
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