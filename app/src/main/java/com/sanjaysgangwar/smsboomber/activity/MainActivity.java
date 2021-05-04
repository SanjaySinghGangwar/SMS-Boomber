package com.sanjaysgangwar.smsboomber.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.sanjaysgangwar.smsboomber.R;
import com.sanjaysgangwar.smsboomber.databinding.ActivityMainBinding;
import com.sanjaysgangwar.smsboomber.model.mSharedPreference;
import com.sanjaysgangwar.smsboomber.service.mProgressView;

import static android.view.View.VISIBLE;
import static com.sanjaysgangwar.smsboomber.service.validation.isValidText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    String phNumber;
    String message;
    int times = 0;
    SmsManager smsManager;
    mProgressView mProgressView;
    Intent intent;
    mSharedPreference sharedPreference;
    AdRequest adRequest;
    String TAG = "SANJAY";
    private InterstitialAd interstitialAd;
    private RewardedAd mRewardedAd;
    private ActivityMainBinding bind;
    private AdView adViewFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initAllComponent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initAds();
        if (sharedPreference.getAds()) {
            bind.adView.loadAd(adRequest);

            bind.adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    bind.adView.setVisibility(VISIBLE);
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    bind.bannerContainer.setVisibility(VISIBLE);
                    bind.bannerContainer.addView(adViewFB);
                    adViewFB.loadAd();
                    Log.i(TAG, "onAdFailedToLoad: " + adError.getMessage());
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }
    }

    private void initAds() {
        MobileAds.initialize(this, initializationStatus -> {

        });
    }

    private void initAllComponent() {
        context = MainActivity.this;
        bind.send.setOnClickListener(this);
        bind.setting.setOnClickListener(this);
        smsManager = SmsManager.getDefault();
        mProgressView = new mProgressView(this);
        sharedPreference = new mSharedPreference(this);

        //ads
        adRequest = new AdRequest.Builder().build();
        AudienceNetworkAds.initialize(this);
        interstitialAd = new InterstitialAd(this, getString(R.string.Facebook_interstitial));
        adViewFB = new AdView(this, getString(R.string.FacebookBannerOne), AdSize.BANNER_HEIGHT_50);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 10);
                } else {
                    validateAllFields();
                }
                break;
            case R.id.setting:
                intent = new Intent(this, Setting.class);
                startActivity(intent);
                break;

        }
    }

    private void validateAllFields() {
        if (isValidText(bind.to.getText().toString(), bind.to) && isValidText(bind.times.getText().toString(), bind.times) && isValidText(bind.message.getText().toString(), bind.message)) {
            times = Integer.parseInt(bind.times.getText().toString().trim());
            mProgressView.showLoader();
            if (sharedPreference.getAds()) {
                showRewardAds();
            } else {
                mProgressView.showLoader();
                sendSms();
            }


        }
    }

    private void showRewardAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.RewardAdd),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        // Handle the error.
                        mProgressView.hideLoader();
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;

                        //add facebook add
                        showFacebookAds();

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        Toast.makeText(context, "Please watch a video while we send messages", Toast.LENGTH_SHORT).show();
                        mProgressView.hideLoader();
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");
                        if (mRewardedAd != null) {
                            Activity activityContext = MainActivity.this;
                            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    mProgressView.showLoader();
                                    sendSms();
                                }

                            });
                        } else {
                            mProgressView.showLoader();
                            sendSms();
                            Log.d(TAG, "The rewarded ad wasn't ready yet.");
                        }
                    }
                });
    }

    private void showFacebookAds() {
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                mProgressView.showLoader();
                sendSms();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                mProgressView.showLoader();
                sendSms();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                validateAllFields();
                break;
        }
    }

    private void sendSms() {
        phNumber = bind.ccp.getSelectedCountryCodeWithPlus() + bind.to.getText().toString().trim();
        message = bind.message.getText().toString().trim();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            times--;
            if (times == 0) {
                bind.to.setText("");
                bind.times.setText("");
                bind.message.setText("");
                bind.send.setVisibility(VISIBLE);
                mProgressView.hideLoader();
                Snackbar snackbar = Snackbar.make(bind.mainLayout, "Send Successfully...", Snackbar.LENGTH_LONG)
                        .setAction("DONATE ❤", view -> {
                            intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://www.buymeacoffee.com/TheAverageGuy"));
                            intent = Intent.createChooser(intent, "Donate us ♥");
                            startActivity(intent);
                        });
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();


            } else {
                smsManager.sendTextMessage(phNumber, null, message, null, null);
                sendSms();
            }
        }, 800);
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}