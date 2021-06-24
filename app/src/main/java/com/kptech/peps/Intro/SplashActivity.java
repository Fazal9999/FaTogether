package com.kptech.peps.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.kptech.peps.R;
import com.kptech.peps.activity.LoginActivity;
import com.kptech.peps.utils.PreferenceStorage;

import static com.kptech.peps.utils.PreferenceStorage.AUTO_LOGIN;
import static com.kptech.peps.utils.PreferenceStorage.LOGIN_STATUS;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    boolean autoLogin = PreferenceStorage.getBooleanPref(SplashActivity.this, AUTO_LOGIN);
                    if (autoLogin) {
                        boolean isLoggedIn = PreferenceStorage.getBooleanPref(SplashActivity.this, LOGIN_STATUS);
                        if (isLoggedIn) {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
