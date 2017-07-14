package com.manvish.sampletest.CommonViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.manvish.sampletest.Application.AadharTimeAttendanceApplication;

public class ManvishAlertDialog {

    //private Context mContext;
    private String title;
    private String message;
    private Activity avt;

    public ManvishAlertDialog(Activity avt, String title, String message) {
        //this.mContext = mContext;
        this.title = title;
        this.message = message;
        this.avt = avt;
    }

    public void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(avt);

        // Setting Dialog Title
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AadharTimeAttendanceApplication.playClick();
                        dialog.cancel();

                    }
                });

        alertDialog.show();

    }

    private ProgressDialog mProgressDialog;
    private void showProgressBar(String msg) {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(avt);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
        }

        String progressBarMsg = msg;
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(progressBarMsg);
        mProgressDialog.show();
    }

    private void dismissProgressBar() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


}
