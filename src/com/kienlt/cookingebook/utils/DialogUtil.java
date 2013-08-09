package com.kienlt.cookingebook.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.kienlt.cookingebook.R;

public class DialogUtil {

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        return progressDialog;
    }
    
    /**
     * Function to show settings alert dialog On pressing Settings button will lauch Settings Options
     * */
    public static void showSettingsAlert(final Activity context, int title, int message, final String action) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(action);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    
    public static void showNoSdAlert(final Activity context, int title, int message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                context.finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    
    public static void showAlertDialog(Context context, DialogInterface.OnClickListener onClickListenner, int message) {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
    	alertDialog.setMessage(message);
    	alertDialog.setPositiveButton("OK", onClickListenner);
    	
    	// Showing Alert Message
    	alertDialog.show();
    }
    
    public static Dialog createConfirmDialog(Context context, DialogInterface.OnClickListener onClickListenner,
              int messageId) {
          return new AlertDialog.Builder(context).setMessage(messageId)
                  .setCancelable(false)
                  .setPositiveButton(R.string.yes, onClickListenner)
                  .setNegativeButton(R.string.no, null).show();
      }
  
      public static Dialog createConfirmDialog(Context context, DialogInterface.OnClickListener onClickListenner,
              String message) {
          return new AlertDialog.Builder(context).setMessage(message)
                  .setCancelable(false)
                  .setPositiveButton(R.string.yes, onClickListenner)
                  .setNegativeButton(R.string.no, null).show();
      }
}
