package com.jeffreytht.gobblet.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jeffreytht.gobblet.R
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

class AdUtil(private val context: Context) {
    private var adsCount: Int = 0
    private var interstitialAd: InterstitialAd? = null
    private var disposable = CompositeDisposable()

    fun loadAds() {
        disposable.add(Single.create<InterstitialAd> {
            InterstitialAd.load(
                context,
                context.getString(R.string.GOOGLE_ADMOB_INTERSTITIAL_UNIT_ID),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        it.onError(Throwable(adError.message))
                    }

                    override fun onAdLoaded(ads: InterstitialAd) {
                        it.onSuccess(ads)
                        ads.fullScreenContentCallback = object :
                            FullScreenContentCallback() {
                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                interstitialAd = null
                            }

                            override fun onAdShowedFullScreenContent() {
                                interstitialAd = null
                            }
                        }
                    }
                })
        }
            .retry()
            .doOnSuccess { interstitialAd = it }
            .subscribe()
        )
    }

    fun showAds(activity: Activity) {
        interstitialAd?.show(activity)
        adsCount = (adsCount + 1) % 5
        if (adsCount % 5 == 0) {
            loadAds()
        }
    }
}

interface AdsCallback {
    fun showAds(adUtil: AdUtil)
}