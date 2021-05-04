package com.sanjaysgangwar.smsboomber.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.sanjaysgangwar.smsboomber.R
import com.sanjaysgangwar.smsboomber.databinding.SettingBinding
import com.sanjaysgangwar.smsboomber.model.mSharedPreference

class Setting : AppCompatActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private lateinit var bind: SettingBinding
    private lateinit var sharedPreference: mSharedPreference
    private var adViewFB: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SettingBinding.inflate(layoutInflater)
        setContentView(bind.root)

        initAllComponents()
    }

    private fun initAllComponents() {
        setUpToolbar()
        sharedPreference = mSharedPreference(this)
        bind.ads.isChecked = sharedPreference.ads
        bind.donate.setOnClickListener(this)
        bind.share.setOnClickListener(this)
        bind.otherApps.setOnClickListener(this)
        bind.ads.setOnCheckedChangeListener(this)

        //ads
        AudienceNetworkAds.initialize(this)
        adViewFB = AdView(this, getString(R.string.FacebookBannerTwo), AdSize.BANNER_HEIGHT_50)
    }

    override fun onResume() {
        super.onResume()
        initAds()
        if (sharedPreference.ads) {
            val adRequest = AdRequest.Builder().build()
            bind.adView.loadAd(adRequest)
            bind.adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    bind.adView.visibility = VISIBLE
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Code to be executed when an ad request fails.
                    bind.bannerContainer.visibility = VISIBLE
                    bind.bannerContainer.addView(adViewFB)
                    adViewFB?.loadAd()

                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            }
        }
    }

    private fun initAds() {
        MobileAds.initialize(this) { initializationStatus: InitializationStatus? -> }
    }

    private fun setUpToolbar() {
        bind.toolbar.title = "Setting"
        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.donate -> {
                intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://www.buymeacoffee.com/TheAverageGuy")
                intent = Intent.createChooser(intent, "Donate us ♥")
                startActivity(intent)
            }
            R.id.share -> {
                intent = Intent()
                intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, Just found a awesome thing on App Store,\n\n" + getString(R.string.Samsung) + packageName) /*https://apps.samsung.com/appquery/appDetail.as?appId=\" + view.getContext().getPackageName()*//*https://www.amazon.com/dp/B08LGSK7WT/ref=apps_sf_sta*/
                startActivity(Intent.createChooser(intent, "Share with"))
            }
            R.id.otherApps -> {
                intent = Intent(this, OtherAppsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.ads -> {
                sharedPreference.ads = isChecked
            }
        }
    }
}