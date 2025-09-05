package com.example.adsxml

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.adsxml.databinding.ActivityMainBinding
import com.example.adsxml.pager.ViewPagerActivity
import com.monetization.adsmain.commons.addNewController
import com.monetization.adsmain.commons.sdkBannerAd
import com.monetization.adsmain.commons.sdkNativeAdd
import com.monetization.adsmain.showRates.full_screen_ads.AdShowOptions
import com.monetization.adsmain.showRates.full_screen_ads.FullScreenAdsShowManager
import com.monetization.adsmain.splash.AdmobSplashAdController
import com.monetization.adsmain.splash.SplashAdType
import com.monetization.appopen.AdmobAppOpenAdsManager
import com.monetization.core.ad_units.core.AdType
import com.monetization.core.commons.Utils.resToView
import com.monetization.core.listeners.UiAdsListener
import com.monetization.core.managers.FullScreenAdsShowListener
import com.monetization.core.ui.LayoutInfo
import com.monetization.core.ui.ShimmerInfo
import com.monetization.interstitials.AdmobInterstitialAdsManager
import com.monetization.interstitials.extensions.InstantInterstitialAdsManager
import com.monetization.nativeads.AdmobNativeAdsManager
import com.remote.firebaseconfigs.RemoteCommons.toConfigString
import com.remote.firebaseconfigs.SdkRemoteConfigController
import com.remote.firebaseconfigs.listeners.SdkConfigListener
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
        AdmobNativeAdsManager.addNewController(
            "Native", listOf("")
        )
        AdmobAppOpenAdsManager.addNewController(
            "AppOpen", listOf("")
        )
        binding.adFrame.canRefreshAdView(false)
        binding.move.setOnClickListener {
            startActivity(
                Intent(this, ViewPagerActivity::class.java)
            )
        }
        binding.showShimmer.setOnClickListener {
            binding.adFrame.showShimmerView(
                shimmer = ShimmerInfo.ShimmerByView(
                    layoutView = com.monetization.nativeads.R.layout.small_native_ad.resToView(
                        this@MainActivity
                    ),
                    addInAShimmerView = true

                ),
                activity = this@MainActivity,
                true
            )
        }
        binding.showNativeAd.setOnClickListener {
            showNativeAd()
        }
        binding.showAd.setOnClickListener {
            showCounterAd {
                startActivity(Intent(this@MainActivity, MainActivity::class.java))
            }
        }

    }

    private fun showSplashAd(
        onAdDismiss: (Boolean) -> Unit
    ) {
        splashAdController.showSplashAd(
            enableKey = true.toConfigString(),
            adType = SplashAdType.AdmobInter("Splash"),
            activity = this,
            lifecycle = lifecycle,
            callBack = object : FullScreenAdsShowListener {
                override fun onAdDismiss(adKey: String, adShown: Boolean, rewardEarned: Boolean) {
                    super.onAdDismiss(adKey, adShown, rewardEarned)
                    Toast.makeText(this@MainActivity, "Did it=$adShown", Toast.LENGTH_SHORT).show()
                    onAdDismiss.invoke(adShown)
                }
            },
            timeInMillis = 10_000
        )

    }

    private fun showCounterAd(onAdDismiss: (Boolean) -> Unit) {
        FullScreenAdsShowManager.showFullScreenAd(
            placementKey = true.toConfigString(),
            key = "Splash",
            requestNewIfAdShown = false,
            normalLoadingTime = 0,
            instantLoadingTime = 10000,
            onAdDismiss = { it, msg ->
                onAdDismiss.invoke(it)
            },
            activity = this@MainActivity,
            adType = AdType.INTERSTITIAL,
            showOptions = AdShowOptions.Instant
        )
    }

    private fun assignConfigs() {
        TestEnabled = SdkRemoteConfigController.getRemoteConfigBoolean("TestEnabled")
        Toast.makeText(this, "TestEnabled=$TestEnabled", Toast.LENGTH_SHORT).show()
    }

    private fun showNativeAd() {

        binding.adFrame.sdkNativeAdd(
            adLayout = LayoutInfo.LayoutByName("large_native_ad"),
            adKey = "Native",
            placementKey = true.toConfigString(),
            showNewAdEveryTime = true,
            showShimmerLayout = ShimmerInfo.GivenLayout(),
            lifecycle = lifecycle,
            requestNewOnShow = false,
            listener = object : UiAdsListener {
                override fun onAdClicked(key: String) {
                    super.onAdClicked(key)
                }
            },
            activity = this,

        )
    }

    private fun showLightDarkMode() {
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
        binding.adFrame.sdkBannerAd(
            activity = this,
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
            },
            fetchOutTimeInSeconds = 8L,
            onUpdate = {}
        )
    }

}