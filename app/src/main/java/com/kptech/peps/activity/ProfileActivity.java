package com.kptech.peps.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kptech.peps.R;
import com.kptech.peps.customview.ExpertAreaCompletionView;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.BackendServerConstants;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.server.firebase.image.ImageUploadHelper;
import com.kptech.peps.server.firebase.image.ImageUploadListener;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.ImageCaptureHelper;
import com.kptech.peps.utils.PermissionRequest;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kptech.peps.utils.ImageCaptureHelper.YOUR_SELECT_PICTURE_REQUEST_CODE;
import static com.kptech.peps.utils.PermissionRequest.REQUEST_EXTERNAL_STORAGE;

public class ProfileActivity extends AppBaseActivity {
    private static final String TAG = ProfileActivity.class.getName();
    private EditText emailEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText, user_id;
    private EditText mDOb, mGender, mIntrestedTopic;
    private List<String> mGenderList = null;
    TextInputLayout hint_uid;
    private ArrayList<String> selectedTopicList = new ArrayList<>();
    private CircleImageView mProfileImage;
    private ImageCaptureHelper mImageHelper;
    private int MY_REQ = 333;
    private LinearLayout interestTopicContainer;
    private ExpertAreaCompletionView expertAreaCompletionView;
    private RadioButton radio_male, radio_female, rad_public, rad_non_public, rad_public1, rad_non_public1, rad_public2, rad_non_public2;
    RadioGroup rad_group, rad_group1, rad_group2;
    RadioGroup radioGroup;
    TextInputLayout heading_name;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        type = getIntent().getStringExtra("type");

        setupArrayList();
        initViews();
    }

    private void initViews() {
        radioGroup = findViewById(R.id.gender_rad);
        ImageView menuImage = (ImageView) findViewById(R.id.back_btn);
        user_id = findViewById(R.id.user_id);
        menuImage.setVisibility(View.VISIBLE);
        rad_public = findViewById(R.id.rad_public);
        rad_group = findViewById(R.id.rad_group);
        rad_non_public = findViewById(R.id.rad_non_public);
        heading_name = findViewById(R.id.heading_name);
        hint_uid = findViewById(R.id.hint_uid);
        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = findViewById(R.id.actionbar_title);
        title.setText(getResources().getString(R.string.profile_txt));

        mProfileImage = (CircleImageView) findViewById(R.id.profile_img1);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionRequest.verifyStoragePermission(ProfileActivity.this)) {
                    startImageCapture();
                }
            }
        });
        mImageHelper = new ImageCaptureHelper(this, this);
        emailEditText = (findViewById(R.id.login_email));
        firstNameEditText = findViewById(R.id.login_first_name);
        lastNameEditText = findViewById(R.id.login_last_name);
        radio_female = findViewById(R.id.radio_female);
        radio_male = findViewById(R.id.radio_male);
        expertAreaCompletionView = findViewById(R.id.interests);
        interestTopicContainer = findViewById(R.id.interest_container);
        expertAreaCompletionView.allowDuplicates(false);
        expertAreaCompletionView.allowCollapse(false);
        expertAreaCompletionView.setClickable(false);
        mDOb = findViewById(R.id.date_of_birth);
        setDateDialog(mDOb);
        mGender = findViewById(R.id.gender);
        mIntrestedTopic = findViewById(R.id.intresets);

        rad_public1 = findViewById(R.id.rad_public1);
        rad_group1 = findViewById(R.id.rad_group1);
        rad_non_public1 = findViewById(R.id.rad_non_public1);

        rad_public2 = findViewById(R.id.rad_public2);
        rad_group2 = findViewById(R.id.rad_group2);
        rad_non_public2 = findViewById(R.id.rad_non_public2);

        mGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Select Gender", mGender, mGenderList);

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mGender.setText("");
            }
        });

        interestTopicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent interestIntent = new Intent(ProfileActivity.this, SelectInterestActivity.class);
                interestIntent.putStringArrayListExtra("SelectedTopics", selectedTopicList);
                startActivityForResult(interestIntent, MY_REQ);
            }
        });


        Button signupButton = findViewById((R.id.signup_button));

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        updateData();

    }

    private void updateData() {
        UserAccount accnt = new UserAccount();
        if (getIntent().getStringExtra("type").equalsIgnoreCase("notes"))
            accnt = new Gson().fromJson(getIntent().getStringExtra("acc"), UserAccount.class);
        else
            accnt = DataHolder.getInstance().getmCurrentUser();

        if (accnt != null) {
            emailEditText.setText(accnt.getEmail());
            String val = accnt.getFirst_name();
            if (DataValidator.isValid(val)) {
                firstNameEditText.setText(val);
            }
            val = accnt.getLast_name();
            if (DataValidator.isValid(val)) {
                lastNameEditText.setText(val);
            }
            val = accnt.getDate_of_birth();
            if (DataValidator.isValid(val)) {
                mDOb.setText(val);
            }
            val = accnt.getGender();
            if (val.equalsIgnoreCase("male"))
                radio_male.setChecked(true);
            else if (val.equalsIgnoreCase("female"))
                radio_female.setChecked(true);

            mGender.setText(accnt.getGender());

            if (accnt.lastName_public)
                rad_public.setChecked(true);
            else rad_non_public.setChecked(true);

            if (accnt.email_public)
                rad_public1.setChecked(true);
            else rad_non_public1.setChecked(true);

            if (accnt.dateOfBirth_public)
                rad_public2.setChecked(true);
            else rad_non_public2.setChecked(true);


            if (getIntent().getStringExtra("type").equals("home")) {
                hint_uid.setHint("User Id");
                if (!accnt.user_id.equals(""))
                    user_id.setText(accnt.user_id);
            }

            if (getIntent().getStringExtra("type").equals("more")) {
                heading_name.setHint("First Name");
                lastNameEditText.setVisibility(View.VISIBLE);
                rad_group.setVisibility(View.VISIBLE);
            } else {
                if (accnt.lastName_public) {
                    heading_name.setHint("Name");
                    firstNameEditText.setText(getIntent().getStringExtra("type"));
                    lastNameEditText.setVisibility(View.GONE);
                    rad_group.setVisibility(View.GONE);
                }
            }

            selectedTopicList = accnt.getInterests();

            if (selectedTopicList != null) {
                expertAreaCompletionView.clear();
                for (int i = 0; i < selectedTopicList.size(); i++) {
                    expertAreaCompletionView.addObject(selectedTopicList.get(i));
                }
            }
        }
    }

    private void setupArrayList() {
        String[] genderList = getResources().getStringArray(R.array.GenderList);
        mGenderList = new ArrayList<>(Arrays.asList(genderList));
    }

    private void startImageCapture() {
        String filename = Long.toString(System.currentTimeMillis());
        mImageHelper.openImageIntent(filename, null, true);
    }

    @Override
    public void onActivityResult(int reqcode, int resultcode, Intent data) {
        Log.d(TAG, " onActivity result");
        super.onActivityResult(reqcode, resultcode, data);
        if ((reqcode == YOUR_SELECT_PICTURE_REQUEST_CODE) && (resultcode == RESULT_OK)) {
            Bitmap bitmap = mImageHelper.doImageCapture(data);
            if ((bitmap != null) && (mImageHelper.getmSelectedImageUri() != null)) {
                Log.d(TAG, "Encoded path is " + mImageHelper.getmSelectedImageUri().toString());
                mProfileImage.setImageBitmap(bitmap);
            }
        } else if (reqcode == MY_REQ) {
            if (selectedTopicList != null) {
                selectedTopicList.clear();
            }
            try {
                selectedTopicList = data.getStringArrayListExtra("selectedTopic");
            } catch (Exception e) {
            }
            if (selectedTopicList.isEmpty())
                return;
            if (expertAreaCompletionView != null) {
                expertAreaCompletionView.clear();
            }
            for (int i = 0; i < selectedTopicList.size(); i++) {
                expertAreaCompletionView.addObject(selectedTopicList.get(i));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "Permission granted");
                    startImageCapture();

                } else {

                    Log.e(TAG, "Permission denied");
                }
                break;
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, new DateChangeListener(edt), year, month, day);
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

        builderSingle.setTitle(title);
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

    ;

    private void updateProfile() {
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        if (accnt != null) {
            showProgressDialog(null, "Uploading Image..");
            if (mImageHelper.getmSelectedImageUri() != null) {
                saveImage(mImageHelper.getmSelectedImageUri());
            }
        } else {
            showErrorAlert(null, BackendServerConstants.USER_NOT_LOGGED_IN);
        }
    }

    private void saveProfile(String imageurl) {
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        if (accnt != null) {
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

            accnt.setGender(mGender.getText().toString().trim());

            try {
                if (selectedTopicList.size() > 0 && selectedTopicList != null) {
                    accnt.setInterests(selectedTopicList);
                }
            } catch (Exception e) {}

            String key = Utils.getKey(accnt.getEmail());
            BackendServer.getInstance().updateProfile(key, accnt, new ResponseReceiver() {
                @Override
                public void onSuccess(int code, Object result) {
                    UserAccount account = (UserAccount) result;
                    DataHolder.getInstance().setmCurrentUser(account);
                    cancelProgressDialog();
                    Toast.makeText(ProfileActivity.this, "Successfully updated profile", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    cancelProgressDialog();
                    Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveImage(Uri inputFilePath) {
        Bitmap bitmap = mImageHelper.getmBitMap();
        String key = Utils.getKey(emailEditText.getText().toString());
        String imagePath = null;
        if (type.equalsIgnoreCase("home")){

            imagePath = ImageUploadHelper.USERS_IMG_PATH + key + ".png";
        }
        if (type.equalsIgnoreCase("worldwide")) {
            imagePath = ImageUploadHelper.USERS_IMG_PATH + "2" + key + ".png";
        }
        if (type.equalsIgnoreCase("newsAccountFeed")) {
            imagePath = ImageUploadHelper.USERS_IMG_PATH + "3" + key + ".png";
        }
        if (type.equalsIgnoreCase("content")) {
            imagePath = ImageUploadHelper.USERS_IMG_PATH + "4" + key + ".png";
        }
        if (type.equalsIgnoreCase("podcast")) {
            imagePath = ImageUploadHelper.USERS_IMG_PATH + "5" + key + ".png";
        }

        ImageUploadHelper.getInstance().uploadImageUsingBitMap(this, inputFilePath, bitmap, imagePath, new ImageUploadListener() {
            @Override
            public void onImageUploaded(Object response) {
                Log.d(TAG, "Image uploaded succesfully " + response.toString());
                String url = (String) response;
                saveProfile(url);
            }

            @Override
            public void onImageUploadFailed(String error) {
                Toast.makeText(ProfileActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                saveProfile(null);

            }
        });

    }

}
