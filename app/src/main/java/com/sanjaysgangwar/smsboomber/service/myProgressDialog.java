package com.sanjaysgangwar.smsboomber.service;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sanjaysgangwar.smsboomber.R;

public class myProgressDialog {
    private final Activity contexT;
    private AlertDialog dialog;

    public myProgressDialog(Activity mContext) {
        contexT = mContext;
    }

    public void show() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(contexT);
        LayoutInflater inflater = contexT.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_dialog, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }
}
