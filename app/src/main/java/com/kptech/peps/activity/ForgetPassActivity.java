package com.kptech.peps.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kptech.peps.R;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.BackendServerConstants;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.DataValidator;

public class ForgetPassActivity extends AppBaseActivity {

    private EditText emailEditText;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        setHeaderView("Forgot Password");
        emailEditText = findViewById(R.id.login_email);
        resetButton = findViewById(R.id.reset_password_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass();
            }
        });
    }


    public void resetPass() {
        String email = emailEditText.getText().toString();
        if (!DataValidator.isDataValid(emailEditText)) {
            emailEditText.setError("Enter valid email address");
            emailEditText.requestFocus();
        } else {
            showProgressDialog(null, null);
            BackendServer.getInstance().forgotPassword(email, new ResponseReceiver() {
                @Override
                public void onSuccess(int code, Object result) {
                    cancelProgressDialog();
                    if (code == BackendServer.RESULT_SUCCESS) {
                        showErrorAlert(null, BackendServerConstants.PASSWORD_RESET_EMAIL);
                    }
                }

                @Override
                public void onError(String error) {
                    cancelProgressDialog();
                    showErrorAlert("PLease enter email", error);
                }
            });
        }
    }


}
