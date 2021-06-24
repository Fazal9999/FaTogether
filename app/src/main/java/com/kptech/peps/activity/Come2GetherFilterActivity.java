package com.kptech.peps.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.kptech.peps.R;
import com.kptech.peps.utils.Utils;

public class Come2GetherFilterActivity extends AppCompatActivity {

    String selectedAge, selectedGender, selectedInterest;
    SharedPreferences.Editor editor;
    boolean maleFilter = true, femaleFilter = true, otherFilter = true, casualFilter = true, datingFilter = true, meetUpsFilter = true, chattingFilter = true;
    SwitchCompat maleSwitch, femaleSwitch, otherSwitch, casualSwitch, datingSwitch, meetUpsSwitch, chattingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_come2gether_filter);

        Spinner spinner = findViewById(R.id.age_spinner);
        maleSwitch = findViewById(R.id.male_switch);
        femaleSwitch = findViewById(R.id.female_switch);
        otherSwitch = findViewById(R.id.other_switch);
        casualSwitch = findViewById(R.id.casual_switch);
        datingSwitch = findViewById(R.id.dating_switch);
        meetUpsSwitch = findViewById(R.id.meetUps_switch);
        chattingSwitch = findViewById(R.id.chatting_switch);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String ageRange = Utils.preferences.getString("filterC2GAge", "showAll");

        if (ageRange != null) {
            selectedAge = ageRange;
        }
        
        spinner.setSelection(0, false);

        setUpSliders();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAge = parent.getItemAtPosition(position).toString();
                editor = Utils.preferences.edit();
                editor.putString("filterC2GAge", selectedAge);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maleFilter = isChecked;
            }
        });

        femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                femaleFilter = isChecked;
            }
        });

        otherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                otherFilter = isChecked;
            }
        });

        casualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                casualFilter = isChecked;
            }
        });

        datingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                datingFilter = isChecked;
            }
        });

        meetUpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meetUpsFilter = isChecked;
            }
        });

        chattingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chattingFilter = isChecked;
            }
        });
    }

    private void setGenderFilter() {
        if (maleFilter && !femaleFilter && !otherFilter) {
            selectedGender = "Male";
        } else if (!maleFilter && femaleFilter && !otherFilter) {
            selectedGender = "Female";
        } else if (!maleFilter && !femaleFilter && otherFilter) {
            selectedGender = "Other";
        } else if (maleFilter && femaleFilter && !otherFilter) {
            selectedGender = "Male/Female";
        } else if (maleFilter && !femaleFilter && otherFilter) {
            selectedGender = "Male/Other";
        } else if (!maleFilter && femaleFilter && otherFilter) {
            selectedGender = "Female/Other";
        } else if (maleFilter && femaleFilter && otherFilter) {
            selectedGender = "showAll";
        } else {
            selectedGender = "Male";
        }

        editor = Utils.preferences.edit();
        editor.putString("filterC2GGender", selectedGender);
        editor.apply();
    }

    private void setInterestFilter(){
        if (casualFilter && !datingFilter && !meetUpsFilter && !chattingFilter) {
            selectedInterest = "Casual";
        } else if (!casualFilter && datingFilter && !meetUpsFilter && !chattingFilter) {
            selectedInterest = "Dating";
        }  else if (!casualFilter && !datingFilter && meetUpsFilter && !chattingFilter) {
            selectedInterest = "MeetUps";
        }  else if (!casualFilter && !datingFilter && !meetUpsFilter && chattingFilter) {
            selectedInterest = "Chatting";
        }  else if (casualFilter && datingFilter && !meetUpsFilter && !chattingFilter) {
            selectedInterest = "Casual/Dating";
        }  else if (casualFilter && !datingFilter && meetUpsFilter && !chattingFilter) {
            selectedInterest = "Casual/MeetUps";
        }  else if (casualFilter && !datingFilter && !meetUpsFilter && chattingFilter) {
            selectedInterest = "Casual/Chatting";
        }  else if (casualFilter && datingFilter && meetUpsFilter && !chattingFilter) {
            selectedInterest = "Casual/Dating/MeetUps";
        }  else if (casualFilter && datingFilter && !meetUpsFilter && chattingFilter) {
            selectedInterest = "Casual/Dating/Chatting";
        }  else if (casualFilter && !datingFilter && meetUpsFilter && chattingFilter) {
            selectedInterest = "Casual/MeetUps/Chatting";
        }  else if (!casualFilter && datingFilter && meetUpsFilter && !chattingFilter) {
            selectedInterest = "Dating/MeetUps";
        }  else if (!casualFilter && datingFilter && meetUpsFilter && chattingFilter) {
            selectedInterest = "Dating/MeetUps/Chatting";
        }  else if (casualFilter && datingFilter && !meetUpsFilter && chattingFilter) {
            selectedInterest = "Dating/Chatting";
        }  else if (!casualFilter && !datingFilter && meetUpsFilter && chattingFilter) {
            selectedInterest = "MeetUps/Chatting";
        }  else if (casualFilter && datingFilter && meetUpsFilter && chattingFilter) {
            selectedInterest = "showAll";
        }  else {
            selectedInterest = "Casual";
        }

        editor = Utils.preferences.edit();
        editor.putString("filterC2GInterest", selectedInterest);
        editor.apply();
    }

    private void setUpSliders(){
        String genderFilter = Utils.preferences.getString("filterC2GGender", "showAll");

        if (genderFilter != null) {
            switch (genderFilter) {
                case "Male":
                    maleSwitch.setChecked(true);
                    femaleSwitch.setChecked(false);
                    otherSwitch.setChecked(false);
                case "Female":
                    maleSwitch.setChecked(false);
                    femaleSwitch.setChecked(true);
                    otherSwitch.setChecked(false);
                case "Other":
                    maleSwitch.setChecked(false);
                    femaleSwitch.setChecked(false);
                    otherSwitch.setChecked(true);
                case "Male/Female":
                    maleSwitch.setChecked(true);
                    femaleSwitch.setChecked(true);
                    otherSwitch.setChecked(false);
                case "Male/Other":
                    maleSwitch.setChecked(true);
                    femaleSwitch.setChecked(false);
                    otherSwitch.setChecked(true);
                case "Female/Other":
                    maleSwitch.setChecked(false);
                    femaleSwitch.setChecked(true);
                    otherSwitch.setChecked(true);
                default:
                    maleSwitch.setChecked(true);
                    femaleSwitch.setChecked(true);
                    otherSwitch.setChecked(true);
            }
        }

        String interestFilter = Utils.preferences.getString("filterC2GInterest", "showAll");

        if (interestFilter != null) {
            switch (interestFilter) {
                case "Casual":
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(false);
                    meetUpsSwitch.setChecked(false);
                    chattingSwitch.setChecked(false);
                case "Dating":
                    casualSwitch.setChecked(false);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(false);
                    chattingSwitch.setChecked(false);
                case "MeetUps":
                    casualSwitch.setChecked(false);
                    datingSwitch.setChecked(false);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(false);
                case "Chatting":
                    casualSwitch.setChecked(false);
                    datingSwitch.setChecked(false);
                    meetUpsSwitch.setChecked(false);
                    chattingSwitch.setChecked(true);
                case "Casual/Dating":
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(false);
                    chattingSwitch.setChecked(false);
                case "Casual/MeetUps":
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(false);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(false);
                case "Casual/Chatting":
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(false);
                    meetUpsSwitch.setChecked(false);
                    chattingSwitch.setChecked(true);
                case "Casual/Dating/MeetUps":
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(false);
                case "Casual/MeetUps/Chatting":
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(false);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(true);
                case "Casual/Dating/Chatting":
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(false);
                    chattingSwitch.setChecked(true);
                case "Dating/MeetUps/Chatting":
                    casualSwitch.setChecked(false);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(true);
                case "Dating/MeetUps":
                    casualSwitch.setChecked(false);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(false);
                case "Dating/Chatting":
                    casualSwitch.setChecked(false);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(false);
                    chattingSwitch.setChecked(true);
                case "MeetUps/Chatting":
                    casualSwitch.setChecked(false);
                    datingSwitch.setChecked(false);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(true);
                default:
                    casualSwitch.setChecked(true);
                    datingSwitch.setChecked(true);
                    meetUpsSwitch.setChecked(true);
                    chattingSwitch.setChecked(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setGenderFilter();
        setInterestFilter();
    }
}