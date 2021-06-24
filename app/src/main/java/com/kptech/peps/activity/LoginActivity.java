package com.kptech.peps.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.kptech.peps.R;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.PreferenceStorage;
import com.kptech.peps.utils.Utils;

import static com.kptech.peps.utils.PreferenceStorage.AUTO_LOGIN;
import static com.kptech.peps.utils.PreferenceStorage.LOGIN_STATUS;

public class LoginActivity extends AppBaseActivity {
    private static final String TAG = LoginActivity.class.getName();
    private EditText emailEditText;
    private EditText passEditText;
    CheckBox tnc_chek;
    TextView tnc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tnc_chek = findViewById(R.id.tnc_chek);
        tnc = findViewById(R.id.tnc);
        initViews();
    }

    private void initViews() {
        emailEditText = (findViewById(R.id.login_email));
        passEditText = (findViewById(R.id.login_pwd));

        Button loginButton = (Button) findViewById(R.id.login_email_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TncView.class);
                intent.putExtra("pdf", "login");
                getActivity().startActivity(intent);
            }
        });
        TextView signupButton = findViewById((R.id.signup_button));
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        TextView forgetPassButton = findViewById(R.id.forget_pass_button);
        forgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgetIntent();
            }
        });

        CheckBox remember = findViewById(R.id.login_remeber_pwd);
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceStorage.saveBooleanPref(LoginActivity.this, AUTO_LOGIN, b);
            }
        });

        //update Data
        String val = PreferenceStorage.getStringPref(this, PreferenceStorage.USER_NAME);
        if (DataValidator.isValid(val)) {
            emailEditText.setText(val);
        }
        val = PreferenceStorage.getStringPref(this, PreferenceStorage.PASSWORD);
        if (DataValidator.isValid(val)) {
            passEditText.setText(val);
        }
        boolean autoLogin = PreferenceStorage.getBooleanPref(this, AUTO_LOGIN);
        remember.setChecked(autoLogin);
        if (autoLogin) {
            boolean isLoggedIn = PreferenceStorage.getBooleanPref(this, LOGIN_STATUS);
            if (isLoggedIn) {
                tnc_chek.setChecked(true);
                performLogin();
            }
        }
    }

    public void performLogin() {
        if (!tnc_chek.isChecked()){
            showErrorAlert("Alert!","Please agree to our term and conditions.");
            return;
        }
        String email = emailEditText.getText().toString();
        String password = passEditText.getText().toString();
        if (!DataValidator.isValidEmail(emailEditText.getText().toString())) {
            emailEditText.setError("Enter valid email address");
            emailEditText.requestFocus();
        } else if (!DataValidator.isDataValid(passEditText)) {
            passEditText.setError("Enter valid password");
            passEditText.requestFocus();
        } else {
            showProgressDialog(null, null);
            BackendServer.getInstance().doLogin(LoginActivity.this, email, password, new ResponseReceiver() {
                @Override
                public void onSuccess(int code, Object result) {
                    if (code == BackendServer.RESULT_SUCCESS && result != null) {
                        UserAccount accnt = (UserAccount) result;
                        if (accnt != null) {
                            DataHolder.getInstance().setmCurrentUser(accnt);
                            PreferenceStorage.saveBooleanPref(LoginActivity.this, LOGIN_STATUS, true);
                            PreferenceStorage.saveStringPref(LoginActivity.this, PreferenceStorage.USER_NAME, emailEditText.getText().toString());
                            PreferenceStorage.saveStringPref(LoginActivity.this, PreferenceStorage.PASSWORD, passEditText.getText().toString());
                            fetchProfile();
                        } else {
                            cancelProgressDialog();
                            showErrorAlert(null, "Unable to sign-in");
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    cancelProgressDialog();
                    showErrorAlert(null, error);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
    }

   /* private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            openHome(user);
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           *//* Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);*//*
                            Toast.makeText(LoginActivity.this, "Facebook Auth failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }*/


    public void signUp() {
        Intent signupIntent = new Intent(this, SignupActivity.class);
        this.startActivity(signupIntent);
        finish();
    }

    public void openForgetIntent() {
        Intent forgetIntent = new Intent(this, ForgetPassActivity.class);
        this.startActivity(forgetIntent);
    }


    public void openHome() {
        Intent home = new Intent(this, HomeActivity.class);
        this.startActivity(home);
        finish();
    }

    private void fetchProfile() {
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        String key = Utils.getKey(accnt.getEmail());
        BackendServer.getInstance().fetchProfile(key, new ResponseReceiver() {
            @Override
            public void onSuccess(int code, Object result) {
                cancelProgressDialog();
                UserAccount accnt = (UserAccount) result;
                DataHolder.getInstance().setmCurrentUser(accnt);
                openHome();
            }

            @Override
            public void onError(String error) {

                cancelProgressDialog();
                showErrorAlert(null, "Error fetching profile");

            }
        });


    }

}
