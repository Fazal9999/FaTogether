package com.kptech.peps.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kptech.peps.R;
import com.kptech.peps.utils.PreferenceStorage;
import com.kptech.peps.utils.Utils;

public class FilterActivity extends AppCompatActivity {

    CheckBox contentCheck;
    SwitchCompat matureSwitch;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        contentCheck = findViewById(R.id.account_check);
        matureSwitch = findViewById(R.id.mature_switch);
        contentCheck.setChecked(Utils.preferences.getBoolean("showContent", false));
        matureSwitch.setChecked(Utils.preferences.getBoolean("hideMature", false));

        contentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = Utils.preferences.edit();
                editor.putBoolean("showContent", isChecked);
                editor.apply();
            }
        });

        matureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = Utils.preferences.edit();
                editor.putBoolean("hideMature", isChecked);
                editor.apply();
            }
        });
    }
}