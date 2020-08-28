package com.sanjaysgangwar.smsboomber;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.muddzdev.styleabletoast.StyleableToast;
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
                    validation();
                }
                break;
        }
    }

    private void validation() {
        countryCode = ccp.getSelectedCountryCodeWithPlus();
        phNumber = Objects.requireNonNull(toEditText.getText()).toString().trim();
        String Value = Objects.requireNonNull(timesEditText.getText()).toString().trim();
        times = Integer.parseInt(Value);

        message = Objects.requireNonNull(messageEditText.getText()).toString();
        if (countryCode.isEmpty() || phNumber.isEmpty() || Value.isEmpty() || message.isEmpty()) {
            new StyleableToast.Builder(context).text("Please provide a valid input").textColor(Color.WHITE).backgroundColor(Color.RED).show();
        } else {
            myProgressDialog.show();
            sendSms();
        }

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, 300);
    }
}