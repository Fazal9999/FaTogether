package com.kptech.peps.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;

import com.kptech.peps.R;


public class AlertDialogHelper {

    public static void showSimpleAlertDialog(Context context, String message) {
        DialogFragment simpleAlertDialogFragment = SimpleAlertDialogFragment.newInstance(
                R.string.app_name, message);
        final Activity activity = (Activity) context;
        if (activity != null) {
            try {
                simpleAlertDialogFragment.setCancelable(true);
                simpleAlertDialogFragment.show(activity.getFragmentManager(), "dialog");
                simpleAlertDialogFragment.setStyle(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Holo_Dialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void showSimpleAlertDialog(Context context, String message, int tag) {
        DialogFragment simpleAlertDialogFragment = SimpleAlertDialogFragment.newInstance(
                R.string.empty, message, tag);
        final Activity activity = (Activity) context;
        if (activity != null) {
            try {
                simpleAlertDialogFragment.setCancelable(false);
                simpleAlertDialogFragment.show(activity.getFragmentManager(), "dialog");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

   /* public static void showAlertDialogForFragment(Context context, DialogClickListener dialogClickListener, String message, int tag) {
        AlertDialogForFragment alertDialogForFragment = AlertDialogForFragment.newInstance(
                R.string.empty, message, tag);
        alertDialogForFragment.setDialogListener(dialogClickListener);
        final Activity activity = (Activity) context;
        if (activity != null)
            try {
                alertDialogForFragment.setCancelable(false);
                alertDialogForFragment.show(activity.getFragmentManager(), "dialog");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }*/

/*
    public static void showCompoundAlertDialog(Context context, String title, String message, String posButton, String negButton, int tag) {
        CompoundAlertDialogFragment compoundDialogFragment = CompoundAlertDialogFragment.newInstance(title, message, posButton, negButton, tag);
        UserActivites activity = (UserActivites)context;
        if(activity != null) {
            compoundDialogFragment.show(activity.getFragmentManager(), "dialog");
        }

    }
*/



}
