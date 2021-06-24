package com.kptech.peps.fragments;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.activity.TncView;
import com.kptech.peps.customview.CustomEditText;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.model.WorldWideProfile;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.server.firebase.image.ImageUploadHelper;
import com.kptech.peps.server.firebase.image.ImageUploadListener;
import com.kptech.peps.utils.PathUtils;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthenticateFragment extends AppBaseFragment {
    private ImageView selfie, frontPage, backPage, postComment;
    private Button uploadSelfie, uploadFrontPage, uploadBackPage;
    CustomEditText worldWideId;
    private String[] cameraPermission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String[] storagePermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int CAMERA_PERMISSION_REQ_CODE = 100;
    private final int STORAGE_PERMISSION_REQ_CODE = 111;
    private final int CAMERA = 2;
    private final int GALLERY = 1;
    private final int SELFIE = 1;
    private final int FRONT_PAGE = 2;
    EditText mWriteComment;
    private final int BACK_PAGE = 3;
    private int ATTACHMENT_MODE;
    private int ATTACHMENT_TYPE;
    private Uri uri;
    private View parentLayout;
    private CustomEditText firstName, lastName, userID, dob;
    private TextView submit;
    private Uri selfieUri, frontPageIDUri, backpageIDuri;
    RelativeLayout message_box;
    WorldWideProfile myProfile = new WorldWideProfile();
    RecyclerView recyclerView;

    CheckBox adult_check;
    CheckBox tnc_chek;
    TextView tnc;

    public AuthenticateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        tnc_chek = view.findViewById(R.id.tnc_chek);
        tnc = view.findViewById(R.id.tnc);

        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TncView.class);
                intent.putExtra("pdf", "worldwide");
                getActivity().startActivity(intent);
            }
        });

        parentLayout = view.findViewById(R.id.container);
        selfie = view.findViewById(R.id.selfie);
        recyclerView = view.findViewById(R.id.post_comment_list);
        frontPage = view.findViewById(R.id.front_page);
        backPage = view.findViewById(R.id.back_page);
        worldWideId = view.findViewById(R.id.user_id);
        firstName = view.findViewById(R.id.first_name);
        mWriteComment = view.findViewById(R.id.et_msg_box);
        postComment = view.findViewById(R.id.send_msg);
        lastName = view.findViewById(R.id.last_name);
        message_box = view.findViewById(R.id.message_box);
        userID = view.findViewById(R.id.user_id);
        submit = view.findViewById(R.id.submit);
        dob = view.findViewById(R.id.date_of_birth);
        selfie.setOnClickListener(this::onClick);
        submit.setOnClickListener(this::onClick);
        backPage.setOnClickListener(this::onClick);
        frontPage.setOnClickListener(this::onClick);
        dob.setOnClickListener(this::onClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        checkIsUploaded();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tag = myProfile.row_key;
                UserAccount account = DataHolder.getInstance().getmCurrentUser();

//                if (account != null) {
//                    comment.setCreated_at(System.currentTimeMillis());
//                    comment.setUpdated_at(System.currentTimeMillis());
//                    comment.setEmail(account.getEmail());
//                    String name = "";
//                    if (account.getFirst_name() != null) {
//                        name += account.getFirst_name();
//                    }
//                    if (account.getLast_name() != null) {
//
//                        name += " " + account.getLast_name();
//                    }
//                    comment.setUser_name(name);
//                    comment.setUser_id(Utils.getKey(account.getEmail()));
//                    comment.setComment_text(mWriteComment.getText().toString());
////                    comment.setProfile_url(account.getImage_url());
////                    comment.setUser_id(account.getWorldwide_user_id());
//                    BackendServer.getInstance().postCommentInRquestWorldwide(comment, tag);
//                    mWriteComment.setText("");
//
//                    adapter.notifyDataSetChanged();
//                }
            }

        });

        return view;
    }


    private void checkIsUploaded() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        DatabaseReference myRef = database.getReference(FirebaseConstants.WORLDWIDE_REQUESTS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    WorldWideProfile model1 = childDataSnapshot.getValue(WorldWideProfile.class);
                    if (model1 != null) {
                        if ((Utils.getKey(accnt.getEmail())).equals(model1.row_key)) {
                            myProfile = model1;
                            message_box.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            setValue();
                            return;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setValue() {

        firstName.setText(myProfile.first_name);
        lastName.setText(myProfile.last_name);
        userID.setText(myProfile.worldwide_user_id);
        dob.setText(myProfile.date_of_birth);

        Picasso.with(getContext())
                .load(myProfile.selfie_id)
                .error(R.drawable.ic_placeholder)
                .into(selfie);

        Picasso.with(getContext())
                .load(myProfile.back_id)
                .error(R.drawable.ic_placeholder)
                .into(backPage);

        Picasso.with(getContext())
                .load(myProfile.front_id)
                .error(R.drawable.ic_placeholder)
                .into(frontPage);


    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.selfie:
                ATTACHMENT_TYPE = SELFIE;
                selectImage();
                break;
            case R.id.front_page:
                ATTACHMENT_TYPE = FRONT_PAGE;
                selectImage();
                break;
            case R.id.back_page:
                ATTACHMENT_TYPE = BACK_PAGE;
                selectImage();
                break;
            case R.id.date_of_birth:
                showDatePicker();
                break;
            case R.id.submit:
                // uploadFrontPageID();
                submitUserDetails();
                break;
        }
    }

    private void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void requestForPermission(int requestCode, String[] permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, requestCode);
        } else {
            if (ATTACHMENT_MODE == CAMERA) {
                cameraIntent();
            } else if (ATTACHMENT_MODE == GALLERY) {
                galleryIntent();
            }
        }
    }

    private void selectImage() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Image From");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ATTACHMENT_MODE = GALLERY;
                                requestForPermission(STORAGE_PERMISSION_REQ_CODE, storagePermission);
                                break;
                            case 1:
                                ATTACHMENT_MODE = CAMERA;
                                requestForPermission(CAMERA_PERMISSION_REQ_CODE, cameraPermission);
                                break;
                        }
                    }
                });
        pictureDialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQ_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                } else {
                    if (!(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                        showSnackbar();
                    }
                }
                break;
            case CAMERA_PERMISSION_REQ_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {
                    if (!(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA))) {
                        showSnackbar();
                    }
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALLERY:
                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        uri = data.getData();
                        String filePath = PathUtils.getPath(getContext(), uri);
                        switch (ATTACHMENT_TYPE) {
                            case SELFIE:
                                selfieUri = uri;
                                uploadSelfie();
                                selfie.setImageURI(uri);
                                break;
                            case FRONT_PAGE:
                                frontPageIDUri = uri;
                                uploadFrontPageID();
                                frontPage.setImageURI(uri);
                                break;
                            case BACK_PAGE:
                                backpageIDuri = uri;
                                uploadBackPageID();
                                backPage.setImageURI(uri);
                                break;
                        }
                    }
                }
                break;
            case CAMERA:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    uri = getImageUri(getContext(), thumbnail);
                    String filePath = PathUtils.getPath(getContext(), uri);
                    File file = new File(filePath);
                    switch (ATTACHMENT_TYPE) {
                        case SELFIE:
                            selfieUri = uri;
                            uploadSelfie();
                            selfie.setImageURI(uri);
                            break;
                        case FRONT_PAGE:
                            frontPageIDUri = uri;
                            frontPage.setImageURI(uri);
                            uploadFrontPageID();
                            break;
                        case BACK_PAGE:
                            backpageIDuri = uri;
                            uploadBackPageID();
                            backPage.setImageURI(uri);
                            break;
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void showSnackbar() {
        Snackbar.make(parentLayout, "Please allow permission", Snackbar.LENGTH_LONG)
                .setAction("ENABLE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAppSettingScreen(getContext());
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    private void openAppSettingScreen(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    private void submitUserDetails() {
        if (!tnc_chek.isChecked()){
            showErrorAlert("Alert!","Please agree to our term and conditions.");
            return;
        }

        showProgressDialog("Uploading user details..", null);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, Object> updateAuthenticationValue = new HashMap<>();
        updateAuthenticationValue.put("first_name", firstName.getText().toString().trim());
        updateAuthenticationValue.put("last_name", lastName.getText().toString().trim());
        updateAuthenticationValue.put("email", user.getEmail());
        updateAuthenticationValue.put("date_of_birth", dob.getText().toString().trim());
        updateAuthenticationValue.put("worldwide_user_id", userID.getText().toString().trim());
        updateAuthenticationValue.put("is_worldwide_available", false);
        updateAuthenticationValue.put("row_key", Utils.getKey(user.getEmail()));
        updateAuthenticationValue.put("status", 1);
        updateAuthenticationValue.put("updated_at", System.currentTimeMillis());

        if(selfieUri == null){
            updateAuthenticationValue.put("selfie_id", "null");
        }

        if(frontPageIDUri == null){
            updateAuthenticationValue.put("front_id", "null");
        }

        if(backpageIDuri == null){
            updateAuthenticationValue.put("back_id", "null");
        }


        BackendServer.getInstance().updateAuthenticationDetails(updateAuthenticationValue, new ResponseReceiver() {
            @Override
            public void onSuccess(int code, Object result) {
                cancelProgressDialog();

                Toast.makeText(getActivity(), "Successfully updated profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                cancelProgressDialog();
                Toast.makeText(getActivity(), "Error updating profile", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void uploadSelfie() {
        if (selfieUri != null) {
            showProgressDialog("Uploading Selfie Image..", null);
            UserAccount userAccount = DataHolder.getInstance().getmCurrentUser();
            ImageUploadHelper.getInstance().uploadImage(getContext(), selfieUri, "WORLDWIDE_REQUESTS/" + Utils.getKey(userAccount.getEmail()) + "SelfieID.png", new ImageUploadListener() {
                @Override
                public void onImageUploaded(Object response) {
                    cancelProgressDialog();
                    HashMap<String, Object> updateAuthenticationValue = new HashMap<>();
                    updateAuthenticationValue.put("selfie_id", response);
                    BackendServer.getInstance().updateAuthenticationDetails(updateAuthenticationValue, new ResponseReceiver() {
                        @Override
                        public void onSuccess(int code, Object result) {
                            cancelProgressDialog();
                            Toast.makeText(getContext(), "Selfie Image Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getActivity(), "Uploading failed", Toast.LENGTH_SHORT).show();
                            cancelProgressDialog();
                        }
                    });
                }

                @Override
                public void onImageUploadFailed(String error) {
                    cancelProgressDialog();
                }
            });
        } else
            Toast.makeText(getContext(), "Please upload Selfie", Toast.LENGTH_SHORT).show();

    }

    private void uploadFrontPageID() {
        if (frontPageIDUri != null) {
            showProgressDialog("Uploading Front Page ID Image..", null);
            UserAccount userAccount = DataHolder.getInstance().getmCurrentUser();
            ImageUploadHelper.getInstance().uploadImage(getContext(), frontPageIDUri, "WORLDWIDE_REQUESTS/" + Utils.getKey(userAccount.getEmail()) + "FrontID.png", new ImageUploadListener() {
                @Override
                public void onImageUploaded(Object response) {
                    cancelProgressDialog();
                    HashMap<String, Object> updateAuthenticationValue = new HashMap<>();
                    updateAuthenticationValue.put("front_id", response);
                    BackendServer.getInstance().updateAuthenticationDetails(updateAuthenticationValue, new ResponseReceiver() {
                        @Override
                        public void onSuccess(int code, Object result) {
                            cancelProgressDialog();
                            Toast.makeText(getActivity(), "Front Page ID Image Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(String error) {
                            Toast.makeText(getActivity(), "Uploading failed", Toast.LENGTH_SHORT).show();
                            cancelProgressDialog();
                        }
                    });
                }

                @Override
                public void onImageUploadFailed(String error) {
                    cancelProgressDialog();
                }
            });
        }
        Toast.makeText(getContext(), "Please upload Front Page ID", Toast.LENGTH_SHORT).show();
    }

    private void uploadBackPageID() {
        if (backpageIDuri != null) {
            showProgressDialog("Uploading Back Page ID Image..", null);
            UserAccount userAccount = DataHolder.getInstance().getmCurrentUser();
            ImageUploadHelper.getInstance().uploadImage(getContext(), backpageIDuri, "WORLDWIDE_REQUESTS/" + Utils.getKey(userAccount.getEmail()) + "BackID.png", new ImageUploadListener() {
                @Override
                public void onImageUploaded(Object response) {
                    HashMap<String, Object> updateAuthenticationValue = new HashMap<>();
                    updateAuthenticationValue.put("back_id", response);
                    BackendServer.getInstance().updateAuthenticationDetails(updateAuthenticationValue, new ResponseReceiver() {
                        @Override
                        public void onSuccess(int code, Object result) {
                            cancelProgressDialog();
                            Toast.makeText(getActivity(), "Back Page ID Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(String error) {
                            Toast.makeText(getActivity(), "Uploading failed", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                @Override
                public void onImageUploadFailed(String error) {

                }
            });
        } else
            Toast.makeText(getContext(), "Please upload Back Page ID", Toast.LENGTH_SHORT).show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DateChangeListener(), year, month, day);
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
    }

    private class DateChangeListener implements DatePickerDialog.OnDateSetListener {
        private SimpleDateFormat mDateFormatter;

        public DateChangeListener() {
            mDateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            if (dob != null)
                dob.setText(mDateFormatter.format(newDate.getTime()));
        }
    }
}
