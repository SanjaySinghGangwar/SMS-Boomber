package com.sanjaysgangwar.smsboomber.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.muddzdev.styleabletoast.StyleableToast;
import com.sanjaysgangwar.smsboomber.R;
import com.sanjaysgangwar.smsboomber.service.myProgressDialog;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.toEditText)
    TextInputEditText toEditText;
    @BindView(R.id.timesEditText)
    TextInputEditText timesEditText;
    @BindView(R.id.messageEditText)
    TextInputEditText messageEditText;
    @BindView(R.id.sendButton)
    Button sendButton;
    String countryCode, phNumber, message;
    int times = 0;
    SmsManager smsManager;
    myProgressDialog myProgressDialog;
    RewardedAd rewardedAd;
    int rewardFlag = 0;
    @BindView(R.id.adViewTwo)
    AdView adViewTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = MainActivity.this;

        ccp.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        smsManager = SmsManager.getDefault();
        myProgressDialog = new myProgressDialog(this);
        initAds();

    }

    private void initAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewTwo.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ccp:
                Toast.makeText(context, "" + ccp.getSelectedCountryCodeWithPlus(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.sendButton:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 10);
                } else {
                    myProgressDialog.show();
                    validation();
                }
                break;
        }
    }

    private void validation() {
        countryCode = ccp.getSelectedCountryCodeWithPlus();
        phNumber = Objects.requireNonNull(toEditText.getText()).toString().trim();
        String Value = Objects.requireNonNull(timesEditText.getText()).toString().trim();
        times = Integer.parseInt(Value) + 1;

        message = Objects.requireNonNull(messageEditText.getText()).toString();
        if (countryCode.isEmpty() || phNumber.isEmpty() || Value.isEmpty() || message.isEmpty()) {
            new StyleableToast.Builder(context).text("Please provide a valid input").textColor(Color.WHITE).backgroundColor(Color.RED).show();
        } else {
            showAdd();
        }

    }

    private void showAdd() {
        rewardedAd = new RewardedAd(this, getString(R.string.RewardAdd));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                if (rewardedAd.isLoaded()) {
                    myProgressDialog.dismiss();
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            Toast.makeText(context, "Watch this full video While we send messages", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            if (rewardFlag == 0) {
                                Toast.makeText(context, "You have to watch that add to send messages", Toast.LENGTH_SHORT).show();
                            } else {
                                myProgressDialog.show();
                                sendSms();
                            }
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            rewardFlag = 1;
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            Toast.makeText(context, "Error Loading ", Toast.LENGTH_SHORT).show();
                            rewardFlag = 1;
                        }
                    };
                    rewardedAd.show(MainActivity.this, adCallback);
                } else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                myProgressDialog.show();
                sendSms();
                Log.d("AddMOb", "onRewardedAdFailedToLoad: " + adError.getMessage());
            }

        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                validation();
                break;
        }
    }

    private void sendSms() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            times--;
            if (times == 0) {
                toEditText.setText("");
                timesEditText.setText("");
                messageEditText.setText("");
                myProgressDialog.dismiss();
                Toast.makeText(context, "Send Successfully...", Toast.LENGTH_SHORT).show();
            } else {
                smsManager.sendTextMessage(phNumber, null, message, null, null);
                sendSms();
            }
        }, 300);
    }
}