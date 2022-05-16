package com.jeffreytht.gobblet.util

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jeffreytht.gobblet.MyApp
import com.jeffreytht.gobblet.R

class AdUtil(private val activity: Activity) {
    private var mInterstitialAd: InterstitialAd? = null

    fun startLoad() {
        InterstitialAd.load(
            activity,
            activity.getString(R.string.GOOGLE_ADMOB_INTERSTITIAL_UNIT_ID),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdError", adError.message)
                    mInterstitialAd = null
                    startLoad()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d("AdError", "Ad was dismissed.")
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Log.d("AdError", "Ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                Log.d("AdError", "Ad showed fullscreen content.")
                                mInterstitialAd = null
                                startLoad()
                            }
                        }
                }
            })
    }

    fun showAds() {
        (activity.application as MyApp).apply {
            if (adsCount % 5 == 0) {
                mInterstitialAd?.show(activity)
            }
            adsCount++
            adsCount %= 5
        }
    }
}