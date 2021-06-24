package com.kptech.peps.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kptech.peps.R;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.model.UserNotes;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.BackendServerConstants;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.server.firebase.image.ImageUploadHelper;
import com.kptech.peps.server.firebase.image.ImageUploadListener;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.ImageCaptureHelper;
import com.kptech.peps.utils.PermissionRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.kptech.peps.utils.ImageCaptureHelper.YOUR_SELECT_PICTURE_REQUEST_CODE;
import static com.kptech.peps.utils.PermissionRequest.REQUEST_EXTERNAL_STORAGE;
//import com.google.android.gms.location.FusedLocationProviderClient;


public class CreateNotesActivity extends AppBaseActivity {
    private static final String TAG = CreateNotesActivity.class.getName();

    private static final int REQUEST_CHECK_SETTINGS = 4;
    RadioButton see_profile_yes, notes_pic_yes;
    private EditText mFrom, mAge, mIam, mLookingFor, mDistance, mSeeking, mNotes,who_can_see,who_can_see_age;
    private RadioGroup mPickSelGroup;
    private ImageView mProfileImage;
    private ImageCaptureHelper mImageHelper;
    private List<String> mGenderList = null;
    private List<String> mLookUpList = null;
    private List<String> mDistanceList = null;
    private List<String> mAgeList = null;
    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notes);
        who_can_see=findViewById(R.id.who_can_see);
        see_profile_yes = findViewById(R.id.see_profile_yes);
        who_can_see_age=findViewById(R.id.who_can_see_age);
        notes_pic_yes=findViewById(R.id.notes_pic_yes);
        setHeaderView("Create Notes");
        checkIfSessionExpired();
        setupArrayList();
        initViews();
        initLocationServices();
    }

    private void initViews() {
        mFrom = findViewById(R.id.notes_from);
        mAge = findViewById(R.id.notes_age);
        mIam = findViewById(R.id.notes_iam);
        mIam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Select Gender", mIam, mGenderList);

            }
        });
        mLookingFor = findViewById(R.id.notes_looking_for);
        mLookingFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Looking For", mLookingFor, mLookUpList);

            }
        });
        mDistance = findViewById(R.id.notes_distance);
        mDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Show Posts within(miles)", mDistance, mDistanceList);

            }
        });
        mSeeking = findViewById(R.id.notes_seeking);
        mSeeking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Select Gender", mSeeking, mGenderList);

            }
        });
        who_can_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Select Gender", who_can_see, mGenderList);

            }
        });
        who_can_see_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsList("Select Gender", who_can_see_age, mAgeList);

            }
        });

        mNotes = findViewById(R.id.notes_txt);
        mPickSelGroup = findViewById(R.id.notes_pic_group);

        mProfileImage = findViewById(R.id.profile_img1);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionRequest.verifyStoragePermission(CreateNotesActivity.this)) {
                    startImageCapture();
                }
            }
        });
        mImageHelper = new ImageCaptureHelper(this, this);
        Button saveBtn = findViewById(R.id.notes_create_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserNotes();
            }
        });

        setUpData();


    }

    private void setupArrayList() {
        String[] genderList = getResources().getStringArray(R.array.GenderList);
        mGenderList = new ArrayList<>(Arrays.asList(genderList));

        String[] lookuplist = getResources().getStringArray(R.array.looking_up_list);
        mLookUpList = new ArrayList<>(Arrays.asList(lookuplist));

        String[] distanceList = getResources().getStringArray(R.array.distance_lookup);

        mDistanceList = new ArrayList<>(Arrays.asList(distanceList));

        String[] ageList = getResources().getStringArray(R.array.age_group_list);

        mAgeList = new ArrayList<>(Arrays.asList(ageList));



    }

    private void startImageCapture() {
        String filename = Long.toString(System.currentTimeMillis());
        mImageHelper.openImageIntent(filename, null, true);

    }

    private void setUpData() {
        UserAccount account = DataHolder.getInstance().getmCurrentUser();
        if (account != null) {
            /*String val = account.getFirst_name();
            if(DataValidator.isValid(val)){
                val += "  ";
            }
            val = account.getLast_name();
            if(DataValidator.isValid(val)){
                val += account.getLast_name();
            }

            if(DataValidator.isValid(val)){
                mFrom.setText(val);
            }else{
                mFrom.setText(account.getEmail());
            }*/

            String val = account.getGender();
            if (DataValidator.isValid(val)) {
                mIam.setText(val);
            }

            if (DataValidator.isValid(account.getFirst_name())) {
                mFrom.setText(account.getFirst_name());
                mFrom.setSelection(mFrom.getText().toString().length());
            }

            //Set the age
            val = account.getDate_of_birth();
            if (DataValidator.isValid(val)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                try {
                    Date userDate = dateFormat.parse(val);
                    if (userDate != null) {
                        Date currentDate = new Date();
                        int age = currentDate.getYear() - userDate.getYear();
                        if (age > 0 && (userDate.getMonth() > currentDate.getMonth())) {
                            age = age - 1;
                        }
                        mAge.setText("" + age);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mAge.setText("");
                }
            }
        }

    }

    private void createUserNotes() {

        if (DataValidator.isDataValid(mFrom) && DataValidator.isDataValid(mIam) && DataValidator.isDataValid(mDistance) &&
                DataValidator.isDataValid(mSeeking) && DataValidator.isDataValid(who_can_see)&&DataValidator.isDataValid(who_can_see_age)&&DataValidator.isDataValid(mNotes)) {

            UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
            if (accnt != null) {
                showProgressDialog(null, "Creating notes");
                if (mImageHelper.getmSelectedImageUri() != null) {
                    saveImage(mImageHelper.getmSelectedImageUri());
                } else {
                   // saveData(accnt.getImage_url());
                }
            } else {
                showErrorAlert(null, BackendServerConstants.USER_NOT_LOGGED_IN);
            }

        }

    }

    private void saveImage(Uri inputFilePath) {
        Bitmap bitmap = mImageHelper.getmBitMap();
        String key = System.currentTimeMillis() + "";
        final String imagePath = ImageUploadHelper.USERS_NOTES_IMG_PATH + key + ".png";
        ImageUploadHelper.getInstance().uploadImageUsingBitMap(this, inputFilePath, bitmap, imagePath, new ImageUploadListener() {
            @Override
            public void onImageUploaded(Object response) {
                Log.d(TAG, "Image uploaded succesfully " + response.toString());
                String url = (String) response;
                saveData(url);
            }

            @Override
            public void onImageUploadFailed(String error) {
                Toast.makeText(CreateNotesActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                saveData(null);

            }
        });

    }

    private void saveData(String url) {
        Log.d(TAG, "Save notes data");
        UserNotes data = new UserNotes();
        data.setUserKey(DataHolder.getInstance().getmCurrentUser().getEmail());
        data.setNotesFrom(mFrom.getText().toString());
        if (DataValidator.isValid(mAge.getText().toString())) {
            data.setAge(mAge.getText().toString());
        }
        data.setiAm(mIam.getText().toString());
        data.setLookingFor(mLookingFor.getText().toString());
        try {
            data.setDistance(Integer.parseInt(mDistance.getText().toString()));
        } catch (Exception e) {

        }
        if (see_profile_yes.isChecked())
            data.allowOther = 1;
        else data.allowOther = 0;
        if (notes_pic_yes.isChecked())
            data.respondWithPic = (true);
        else data.respondWithPic = (false);

        data.setCreated_at(System.currentTimeMillis());
        data.setSeeking(mSeeking.getText().toString());
        data.setNotes(mNotes.getText().toString());
        data.whocanseeage=who_can_see_age.getText().toString();
        data.whocanseegender=who_can_see.getText().toString();
        if (mCurrentLocation != null && (mCurrentLocation.getLongitude() != 0 || mCurrentLocation.getLongitude() != 0)) {
            com.kptech.peps.model.Location loc = new com.kptech.peps.model.Location();
            loc.setLat(mCurrentLocation.getLatitude());
            loc.setLng(mCurrentLocation.getLongitude());
            data.setUserLocation(loc);
        }

        if (DataValidator.isValid(url)) {
            data.setImageUrl(url);
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

            edtTxtView.setText(list.get(which));

        }
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
        } else if (reqcode == REQUEST_CHECK_SETTINGS) {
            if (PermissionRequest.verifyLocationPermission(this)) {
                fetchCurrentLocation();
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

    private void initLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        checkIfAllLocationSettingsAreEnabled();

    }

    private void checkIfAllLocationSettingsAreEnabled() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(6000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    if (response != null && response.getLocationSettingsStates() != null &&
                            response.getLocationSettingsStates().isLocationPresent() && response.getLocationSettingsStates().isLocationUsable() &&
                            response.getLocationSettingsStates().isGpsUsable() && response.getLocationSettingsStates().isGpsPresent()) {

                        //registerForLocationUpdates();
                        if (PermissionRequest.verifyLocationPermission(CreateNotesActivity.this)) {
                            fetchCurrentLocation();

                        }
                    }
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            Log.d(TAG, "On Resolution reqd error");
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        CreateNotesActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            Log.e(TAG, "Settings change unavailable");

                            break;
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void fetchCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = location;

                } else {
                    Log.e(TAG, "current location returned is null");
                    showErrorAlert(null, "Check if Location services are enabled");
                }
            }
        });
    }
}
