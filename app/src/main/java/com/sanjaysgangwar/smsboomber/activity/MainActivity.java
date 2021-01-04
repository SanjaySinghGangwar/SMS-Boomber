package com.sanjaysgangwar.smsboomber.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.sanjaysgangwar.smsboomber.R;
import com.sanjaysgangwar.smsboomber.databinding.ActivityMainBinding;
import com.sanjaysgangwar.smsboomber.service.myProgressDialog;

import static com.sanjaysgangwar.smsboomber.service.validation.isValidPhone;
import static com.sanjaysgangwar.smsboomber.service.validation.isValidText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    String phNumber;
    String message;
    int times = 0;
    SmsManager smsManager;
    myProgressDialog myProgressDialog;
    Intent intent;
    private ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        context = MainActivity.this;

        bind.ccp.setOnClickListener(this);
        bind.send.setOnClickListener(this);

        smsManager = SmsManager.getDefault();
        myProgressDialog = new myProgressDialog(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ccp:
                Toast.makeText(context, "" + bind.ccp.getSelectedCountryCodeWithPlus(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.send:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 10);
                } else {
                    if (isValidText(bind.times.getText().toString(), bind.times)) {
                        times = Integer.parseInt(bind.times.getText().toString());
                        myProgressDialog.show();
                        sendSms();
                    }

                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (isValidText(bind.times.getText().toString(), bind.times)) {
                    times = Integer.parseInt(bind.times.getText().toString());
                    myProgressDialog.show();
                    sendSms();
                }
                break;
        }
    }

    private void sendSms() {
        if (isValidPhone(bind.to.getText().toString(), bind.to) && isValidText(bind.message.getText().toString(), bind.message)) {
            phNumber = bind.ccp.getSelectedCountryCodeWithPlus() + bind.to.getText().toString().trim();
            message = bind.message.getText().toString().trim();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                times--;
                if (times == 0) {
                    bind.to.setText("");
                    bind.times.setText("");
                    bind.message.setText("");
                    bind.send.setVisibility(View.VISIBLE);
                    myProgressDialog.dismiss();
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
            }, 300);
        }

    }
}