package com.kptech.peps.activity;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.kaopiz.kprogresshud.KProgressHUD;
import com.kptech.peps.R;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServerConstants;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.AlertDialogHelper;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.DialogClickListener;

import static com.kptech.peps.server.BackendServerConstants.UNABLE_TO_PERFORM_OPERATION;


public class AppBaseActivity extends AppCompatActivity implements DialogClickListener, ResponseReceiver {
    protected KProgressHUD mProgressDialog;

    protected void setHeaderView(String titleMsg){
    /*    ImageView menuImage = (ImageView) findViewById(R.id.back_btn);
//        menuImage.setVisibility(View.VISIBLE);
        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        TextView title = findViewById(R.id.actionbar_title);
        if(DataValidator.isValid(titleMsg)) {
           // title.setText("");
        }

    }

    protected void showProgressDialog(String title, String msg){
        mProgressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                ;
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

    protected Context getActivity(){
        return this;
    }

    protected void showErrorAlert(String title, String msg){
        if(!DataValidator.isValid(msg)){
            msg = "Error performing operation";
        }
        AlertDialogHelper.showSimpleAlertDialog(this,msg);
    }
    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onSuccess(int code, Object result) {
        if(code == BackendServerConstants.SUCCESS && result != null){
            onSuccess(result);

        }else{
            showErrorAlert(null,"Error");
        }
    }

    public void onSuccess( Object result) { }

    @Override
    public void onError(String error) {
        cancelProgressDialog();
        if(DataValidator.isValid(error)){
            showErrorAlert(null,error);
        }else{
            showErrorAlert(null,UNABLE_TO_PERFORM_OPERATION);
        }

    }

    protected void checkIfSessionExpired(){
        UserAccount account = DataHolder.getInstance().getmCurrentUser();
        if(account == null){
            Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }
    }
}
