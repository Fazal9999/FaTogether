package com.kptech.peps.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kptech.peps.R;
import com.kptech.peps.customview.ExpertAreaCompletionView;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppBaseActivity {
    private static final String TAG = SignupActivity.class.getName();

    private EditText emailEditText;
    private EditText passEditText;
    private EditText passConfirmEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText, userIdEditText;
    private EditText mDOb, mGender;
    private List<String> mGenderList = null;
    private RadioButton radio_male, radio_female, radio_other;
    private RadioGroup radioGroup;
    private TextInputLayout gender_lay;
    private int MY_REQ = 333;
    CheckBox tnc_chek;
    private ArrayList<String> selectedTopicList = new ArrayList<>();
    private LinearLayout interestTopicContainer;
    private ExpertAreaCompletionView expertAreaCompletionView;
    TextView tnc;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        tnc_chek = findViewById(R.id.tnc_chek);
        tnc = findViewById(R.id.tnc);

        setupArrayList();
        initViews();
    }

    private void initViews() {
        emailEditText = (findViewById(R.id.login_email));
        passEditText = (findViewById(R.id.login_pwd));
        passConfirmEditText = findViewById(R.id.login_confirm_pwd);
        firstNameEditText = findViewById(R.id.login_first_name);
        lastNameEditText = findViewById(R.id.login_last_name);
        radioGroup = findViewById(R.id.gender_rad);
        radio_female = findViewById(R.id.radio_female);
        userIdEditText = findViewById(R.id.user_id);
        radio_male = findViewById(R.id.radio_male);
        expertAreaCompletionView = findViewById(R.id.interests);
        interestTopicContainer = findViewById(R.id.interest_container);

        mDOb = findViewById(R.id.date_of_birth);
        setDateDialog(mDOb);
        mGender = findViewById(R.id.gender);
        Button signupButton = findViewById((R.id.signup_button));
        expertAreaCompletionView.allowDuplicates(false);
        expertAreaCompletionView.allowCollapse(false);
        expertAreaCompletionView.setClickable(false);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mGender.setText("");
            }
        });
        TextView loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Select Gender", mGender, mGenderList);
            }
        });
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, TncView.class));
            }
        });

        interestTopicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent interestIntent = new Intent(SignupActivity.this, SelectInterestActivity.class);
                interestIntent.putStringArrayListExtra("SelectedTopics", selectedTopicList);
                startActivityForResult(interestIntent, MY_REQ);
            }
        });
    }

    private void setupArrayList() {
        String[] genderList = getResources().getStringArray(R.array.GenderList);
        mGenderList = new ArrayList<>(Arrays.asList(genderList));
    }

    public void signUp() {
        if (!tnc_chek.isChecked()){
            showErrorAlert("Alert!","Please agree to our term and conditions.");
            return;
        }
        final String email = emailEditText.getText().toString();
        String password = passEditText.getText().toString();
        String confirmPass = passConfirmEditText.getText().toString();

        if (firstNameEditText.getText().toString().trim().length() < 1) {
            showErrorAlert(null, "Enter Valid First Name");
        }else if (lastNameEditText.getText().toString().trim().length() < 1) {
            showErrorAlert(null, "Enter Valid Last Name");
        }else if (mDOb.getText().toString().trim().length() < 1) {
            showErrorAlert(null, "Enter Valid Date of birthday");
        }else if (!DataValidator.isValidEmail(emailEditText.getText().toString())) {
            showErrorAlert(null, "Not a valid email");
        }  else if (userIdEditText.getText().toString().trim().length() < 1) {
            showErrorAlert(null, "Enter Valid User Id");
        }else if (selectedTopicList.size() < 1) {
            showErrorAlert(null, "Enter Valid Interests");
        }else if (passEditText.getText().toString().trim().length() < 1) {
            showErrorAlert(null, "Enter Valid  Password");
        }
        else if (!password.equals(confirmPass)) {
            showErrorAlert(null, "Password and Confirm Password are not same");
        } else {
            showProgressDialog(null, null);
            UserAccount accnt = new UserAccount();
            accnt.setEmail(email);
            accnt.setUser_id(userIdEditText.getText().toString().trim());
//            accnt.setUpdated_at(System.currentTimeMillis());
//            accnt.setCreated_at(System.currentTimeMillis());

//            accnt.setUser_full_name(firstNameEditText.getText().toString() +" "+ lastNameEditText.getText().toString());

            String val = firstNameEditText.getText().toString();
            if (DataValidator.isValid(val)) {
                accnt.setFirst_name(val);
            }
            val = lastNameEditText.getText().toString();
            if (DataValidator.isValid(val)) {
                accnt.setLast_name(val);
            }
            val = mDOb.getText().toString();
            if (DataValidator.isValid(val)) {
                accnt.setDate_of_birth(val);
            }


            if (radio_female.isChecked()) {
                accnt.setGender("Female");
            } else if (radio_male.isChecked()) {
                accnt.setGender("Male");
            }

           // accnt.gender_other = (mGender.getText().toString().trim());

            if (selectedTopicList.size() > 0) {
                accnt.setInterests(selectedTopicList);
            }

           // accnt.setUid(Utils.getKey(email));

            BackendServer.getInstance().signup(this, accnt, passEditText.getText().toString(), new ResponseReceiver() {
                @Override
                public void onSuccess(int code, Object result) {
                    if (code == BackendServer.RESULT_SUCCESS && result != null) {
                        cancelProgressDialog();
                        UserAccount accnt = (UserAccount) result;
                        if (accnt != null) {
                            DataHolder.getInstance().setmCurrentUser(accnt);
                        }

                        // grab UID THEN save to firebase USERS
                        openHome();
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

    private void setDateDialog(final EditText edt) {
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR,-18);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, new DateChangeListener(edt), year, month, day);
                Calendar pastCalendar = Calendar.getInstance();
                pastCalendar.add(Calendar.YEAR,-18);
                datePickerDialog.getDatePicker().setMaxDate(pastCalendar.getTimeInMillis());
                datePickerDialog.show();

            }
        });
    }

    private class DateChangeListener implements DatePickerDialog.OnDateSetListener {
        private EditText edtTxt;
        private SimpleDateFormat mDateFormatter;

        DateChangeListener(EditText edttag) {
            edtTxt = edttag;
            mDateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            if (edtTxt != null)
                edtTxt.setText(mDateFormatter.format(newDate.getTime()));
        }
    }

    private void showOptionsList(String title, EditText edt, List<String> list) {
        androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(this);
        AlertListener listener = new AlertListener(list, edt);
        builderSingle.setAdapter(new ArrayAdapter<String>(this, R.layout.options_row, R.id.name, list),
                listener);
        builderSingle.show();
    }

    private class AlertListener implements DialogInterface.OnClickListener {
        private List<String> list;
        private EditText edtTxtView;

        public AlertListener(List<String> items, EditText edt) {
            list = items;
            edtTxtView = edt;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String gnder = "";
            if (radio_female.isChecked()) {
                gnder = ("Female");
            } else {
                gnder = ("Male");
            }
            edtTxtView.setText(list.get(which) + " " + gnder);
        }
    }

    public void openHome() {
        Intent home = new Intent(this, HomeActivity.class);
        this.startActivity(home);
        finish();
    }

    public void login() {
        Intent login = new Intent(this, LoginActivity.class);
        this.startActivity(login);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MY_REQ) {
                selectedTopicList = data.getStringArrayListExtra("selectedTopic");
                expertAreaCompletionView.clear();
                for (int i = 0; i < selectedTopicList.size(); i++) {
                    expertAreaCompletionView.addObject(selectedTopicList.get(i));
                }
                if (selectedTopicList.size() > 0)
                    expertAreaCompletionView.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();

        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you really finish this app?");
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {    // this guy is serious
            // clean up
            super.onBackPressed();       // bye
        }
    }
}
