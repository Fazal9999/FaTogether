package com.kptech.peps.fragments;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.kptech.peps.R;
import com.kptech.peps.utils.AlertDialogHelper;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.DialogClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class  AppBaseFragment extends Fragment implements DialogClickListener {


    public AppBaseFragment() {
        // Required empty public constructor
    }

    protected KProgressHUD mProgressDialog;

    protected void showProgressDialog(String title, String msg){
        mProgressDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        if(DataValidator.isValid(title)){
            mProgressDialog.setLabel(title);
        }
        if(DataValidator.isValid(msg)){
            mProgressDialog.setDetailsLabel(msg);
        }
        mProgressDialog.show();
    }

    protected void cancelProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    protected void showErrorAlert(String title, String msg){
        AlertDialogHelper.showSimpleAlertDialog(getActivity(),msg);
    }
    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
