package com.kptech.peps.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;
import com.kptech.peps.R;
import com.kptech.peps.customview.CustomEditText;
import com.kptech.peps.customview.ExpertAreaCompletionView;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.server.firebase.image.ImageUploadHelper;
import com.kptech.peps.server.firebase.image.ImageUploadListener;
import com.kptech.peps.utils.Constants;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.PathUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateNewsPostActivity extends AppBaseActivity {
    LinearLayout upload_lay;
    Uri savedUri;
    private final int MY_REQ = 333;
    private ArrayList<String> selectedTopicList = new ArrayList<>();
    private LinearLayout interestTopicContainer;
    private ExpertAreaCompletionView expertAreaCompletionView;
    private TextView selectTopicTitle;
    EditText mUrl;
    private LinearLayout parentLayout;
    private String[] cameraPermission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int CAMERA_PERMISSION_REQ_CODE = 100;
    private String[] storagePermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private TextView createPost;
    private final int Gallary = 3;
    private final int VIDEO = 2;
    private final int IMAGE = 1;
    private int ATTACHMENT_MODE = 0;
    private Uri contentUri, thumbcontentUri;
    Bitmap thumb;
    private CheckBox isAdultPost;
    private CustomEditText feedDesc;

    CircleImageView img_placeholder;
    ImageView uploaded_img;
    TextView uploaded_txt;

    VideoView jcVideoPlayerStandard;
    ImageView play_button;
    PostDetails data;

    EditText start_at, end_at;
    Long stat_timestamp  = System.currentTimeMillis(), end_timestamp = System.currentTimeMillis();
    TimePickerDialog picker;
    String Gallary_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ww_create_post);

        mUrl = findViewById(R.id.tv_url);
        uploaded_img = findViewById(R.id.uploaded_img);
        img_placeholder = findViewById(R.id.img_placeholder);
        uploaded_txt = findViewById(R.id.uploaded_txt);

        play_button = findViewById(R.id.play_button);
        jcVideoPlayerStandard = findViewById(R.id.videoplayer);
        upload_lay = findViewById(R.id.upload_lay);
        expertAreaCompletionView = findViewById(R.id.interests);
        expertAreaCompletionView.allowDuplicates(false);
        expertAreaCompletionView.allowCollapse(false);
        expertAreaCompletionView.setClickable(false);
        parentLayout = findViewById(R.id.parent_layout);
        interestTopicContainer = findViewById(R.id.interest_container);
        selectTopicTitle = findViewById(R.id.select_topic_title);
        createPost = findViewById(R.id.create_post);
        feedDesc = findViewById(R.id.feed_desc);
        isAdultPost = findViewById(R.id.check_adult);

        start_at = findViewById(R.id.start_at);
        end_at = findViewById(R.id.end_at);

        start_at.setInputType(InputType.TYPE_NULL);
        start_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(CreateNewsPostActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                start_at.setText(sHour + " hrs : " + sMinute + " mins");

                                final Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.HOUR_OF_DAY, sHour);
                                calendar1.set(Calendar.MINUTE, sMinute);
                                stat_timestamp = calendar1.getTimeInMillis();
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        end_at.setInputType(InputType.TYPE_NULL);
        end_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(CreateNewsPostActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                end_at.setText(sHour + " hrs : " + sMinute + " mins");
                                final Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.HOUR_OF_DAY, sHour);
                                calendar1.set(Calendar.MINUTE, sMinute);

                                end_timestamp  = calendar1.getTimeInMillis();
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        if (getIntent().getBooleanExtra("edit", false)) {
            data = new Gson().fromJson(getIntent().getStringExtra("model"), PostDetails.class);
            start_at.setVisibility(View.GONE);
            end_at.setVisibility(View.GONE);
            setValue(data);
        }

        if (getIntent().getBooleanExtra("edit", false))
            setHeaderView("Edit News Post");
        else setHeaderView("Create News Post");

       // interestTopicContainer.setVisibility(View.GONE);
        interestTopicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent interestIntent = new Intent(CreateNewsPostActivity.this, SelectInterestActivity.class);
                interestIntent.putStringArrayListExtra("SelectedTopics", selectedTopicList);
                startActivityForResult(interestIntent, MY_REQ);
            }
        });

        upload_lay.setOnClickListener(v -> selectContent());
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTopicList == null || selectedTopicList.size() == 0) {
                    Toast.makeText(CreateNewsPostActivity.this, "Please select interests!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!getIntent().getBooleanExtra("edit", false))
                    createHomePost();
                else upatePost(data);

            }
        });


    }

    private void setValue(PostDetails data) {

        if (DataValidator.isValid(data.getDescription())) {
            feedDesc.setText(data.getDescription());
            feedDesc.setSelection(data.getDescription().length());
        }

        isAdultPost.setChecked(data.isIs_mature_content());


        img_placeholder.setVisibility(View.GONE);
        uploaded_txt.setVisibility(View.VISIBLE);
        uploaded_img.setVisibility(View.VISIBLE);

           /* if (!DataValidator.isValid(data.getSource_path()))
                return;
            savedUri = Uri.parse(data.getSource_path());
            img_placeholder.setVisibility(View.GONE);
            uploaded_txt.setVisibility(View.GONE);

            jcVideoPlayerStandard.setVisibility(View.VISIBLE);
            play_button.setVisibility(View.VISIBLE);

            RequestOptions options = new RequestOptions().frame(1000);


            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    play_button.setVisibility(View.GONE);
                    uploaded_img.setVisibility(View.GONE);
                    jcVideoPlayerStandard.setVideoURI(savedUri);
                    jcVideoPlayerStandard.requestFocus();
                    jcVideoPlayerStandard.start();
                }
            });
            jcVideoPlayerStandard.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    play_button.setVisibility(View.VISIBLE);
                }
            });*/
            if (!DataValidator.isValid(data.getPost_image()))
                return;

            savedUri = Uri.parse(data.getPost_image());
/*

            jcVideoPlayerStandard.setVisibility(View.GONE);
            play_button.setVisibility(View.GONE);
            uploaded_img.setVisibility(View.VISIBLE);
            img_placeholder.setVisibility(View.GONE);
            uploaded_txt.setVisibility(View.GONE);
*/

            Picasso.with(this)
                    .load(savedUri)
                    .error(R.drawable.post_placeholder)
                    .into(uploaded_img);

//        if (data.getInterests() != null) {
//            selectedTopicList = data.getInterests();
//            expertAreaCompletionView.clear();
//
//            for (int i = 0; i < selectedTopicList.size(); i++) {
//                expertAreaCompletionView.addObject(selectedTopicList.get(i));
//            }
//            if (selectedTopicList.size() > 0) {
//                expertAreaCompletionView.setVisibility(View.VISIBLE);
//
//
//            } else {
//                expertAreaCompletionView.setVisibility(View.GONE);
//            }
//        }
    }


    private void selectContent() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(CreateNewsPostActivity.this);
        pictureDialog.setTitle("Select Image From");
        String[] pictureDialogItems = {
                "Image",
                "Video",
                "Gallery"
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ATTACHMENT_MODE = IMAGE;
                                break;
                            case 1:
                                ATTACHMENT_MODE = VIDEO;
                                break;
                            case 2:
                                Log.d("gallary", "onClick: ");
                                ATTACHMENT_MODE = Gallary;
                                break;
                        }
                        requestForPermission(CAMERA_PERMISSION_REQ_CODE, cameraPermission);
                    }
                });
        pictureDialog.show();
    }

    private void requestForPermission(int requestCode, String[] permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, requestCode);
        } else {
            if (ATTACHMENT_MODE == IMAGE) {
                imageIntent();
            } else if (ATTACHMENT_MODE == VIDEO) {
                videoIntent();
            }else if(ATTACHMENT_MODE == Gallary){
                gallaryIntent();
            }
        }
    }

    private void imageIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE);
    }

    private void videoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Intent chooserIntent = Intent.createChooser(takeVideoIntent, "Capture Video");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takeVideoIntent});
        startActivityForResult(chooserIntent, VIDEO);
    }

    private void gallaryIntent() {
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Gallary);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQ_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (ATTACHMENT_MODE == IMAGE) {
                        imageIntent();
                    } else if (ATTACHMENT_MODE == VIDEO) {
                        videoIntent();
                    } else if (ATTACHMENT_MODE == Gallary) {
                        gallaryIntent();
                    }
                } else {
                    if (!(ActivityCompat.shouldShowRequestPermissionRationale(CreateNewsPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(CreateNewsPostActivity.this, Manifest.permission.CAMERA))) {
                        showSnackbar();
                    }
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_REQ:
                if (data != null) {

                    selectedTopicList = data.getStringArrayListExtra("selectedTopic");
                    expertAreaCompletionView.clear();
                    for (int i = 0; i < selectedTopicList.size(); i++) {
                        expertAreaCompletionView.addObject(selectedTopicList.get(i));
                    }
                    if (selectedTopicList.size() > 0) {
                        expertAreaCompletionView.setVisibility(View.VISIBLE);

                    } else {
                        expertAreaCompletionView.setVisibility(View.GONE);
                    }
                }
                break;
            case VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filePath = PathUtils.getPath(getActivity(), uri);
                    File file = new File(filePath);
                    long file_size = (file.length() / (1024 * 1024));
                    if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                        contentUri = uri;
                        savedUri = null;
                        uploaded_img.setVisibility(View.GONE);
                        img_placeholder.setVisibility(View.GONE);
                        uploaded_txt.setVisibility(View.GONE);
                        jcVideoPlayerStandard.setVisibility(View.VISIBLE);
                        play_button.setVisibility(View.VISIBLE);

                        thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);

                        thumbcontentUri = getImageUri(CreateNewsPostActivity.this, thumb);


                        play_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                play_button.setVisibility(View.GONE);
                                jcVideoPlayerStandard.setVideoURI(contentUri);
                                jcVideoPlayerStandard.requestFocus();
                                jcVideoPlayerStandard.start();
                            }
                        });

                        jcVideoPlayerStandard.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                play_button.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        Toast.makeText(CreateNewsPostActivity.this, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case IMAGE:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                    Uri uri = getImageUri(CreateNewsPostActivity.this, thumbnail);

                    String filePath = PathUtils.getPath(getActivity(), uri);
                    File file = new File(filePath);
                    long file_size = (file.length() / (1024 * 1024));
//                    if (file_size <= 5) {
                        contentUri = uri;
                        jcVideoPlayerStandard.setVisibility(View.GONE);
                        play_button.setVisibility(View.GONE);
                        savedUri = null;
                        uploaded_img.setVisibility(View.VISIBLE);
                        img_placeholder.setVisibility(View.GONE);
                        uploaded_txt.setVisibility(View.GONE);
                        uploaded_img.setImageURI(contentUri);
                    } else {
                        Toast.makeText(CreateNewsPostActivity.this, "Size cant be more than 5 mb", Toast.LENGTH_LONG).show();
                    }
                    Log.d("IMAGE_URI", contentUri.toString());
//                }
                break;
            case Gallary:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();

                    if (uri.toString().contains("image")) {
                        Gallary_type = "image";

                        //handle image
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if ((bitmap != null) && (uri != null)) {

                            savedUri = null;
                            contentUri = uri;
                            uploaded_img.setVisibility(View.VISIBLE);
                            jcVideoPlayerStandard.setVisibility(View.GONE);
                            img_placeholder.setVisibility(View.GONE);
                            uploaded_txt.setVisibility(View.GONE);
                            uploaded_img.setImageURI(contentUri);
                            play_button.setVisibility(View.GONE);
                        }

                    } else  if (uri.toString().contains("video")) {
                        Gallary_type = "video";
                        String filePath = PathUtils.getPath(getActivity(), uri);
                        File file = new File(filePath);
                        long file_size = (file.length() / (1024 * 1024));
//                        if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                            contentUri = uri;
                            savedUri = null;
                            uploaded_img.setVisibility(View.GONE);
                            img_placeholder.setVisibility(View.GONE);
                            uploaded_txt.setVisibility(View.GONE);
                            jcVideoPlayerStandard.setVisibility(View.VISIBLE);
                            play_button.setVisibility(View.VISIBLE);

                            thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);

                            thumbcontentUri = getImageUri(CreateNewsPostActivity.this, thumb);


                            play_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    play_button.setVisibility(View.GONE);
                                    jcVideoPlayerStandard.setVideoURI(contentUri);
                                    jcVideoPlayerStandard.requestFocus();
                                    jcVideoPlayerStandard.start();
                                }
                            });

                            jcVideoPlayerStandard.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    play_button.setVisibility(View.VISIBLE);
                                }
                            });
//
//                        } else {
//                            Toast.makeText(CreateNewsPostActivity.this, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show();
//                        }
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
                        openAppSettingScreen(CreateNewsPostActivity.this);
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

    private void createHomePost() {
        showProgressDialog(null, null);
        UserAccount userAccount = DataHolder.getInstance().getmCurrentUser();
        PostDetails postDetails = new PostDetails();
        String outputFile = "";
        String thumbImgFile = "";
        if (selectedTopicList.size()==0)
        {Toast.makeText(this,"Please select interest",Toast.LENGTH_SHORT).show();
            return;}

        postDetails.setIs_mature_content(isAdultPost.isChecked());
        postDetails.setDescription(feedDesc.getText().toString());

        if (ATTACHMENT_MODE != 0) {
            if (ATTACHMENT_MODE == IMAGE) {
              //  postDetails.setSource_type("image");
                outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png";
            } else if (ATTACHMENT_MODE == VIDEO) {
              //  postDetails.setSource_type("video");
                outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".mp4";
                thumbImgFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png";

            }
            else if (ATTACHMENT_MODE == Gallary) {
                if ( Gallary_type.equalsIgnoreCase("image")){
                 //  postDetails.setSource_type("image");
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png";
                } else{
                  //  postDetails.setSource_type("video");
                    outputFile =FirebaseConstants.POSTS_LIST + "/"+ System.currentTimeMillis() + ".mp4";
                    thumbImgFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png";
                }
            }
        }

        //postDetails.set(userAccount.getProfilePic());
        postDetails.setUser_ID("newsAccountFeed");
//        postDetails.setStart_at(stat_timestamp);
//        postDetails.setEnd_at(end_timestamp);

        if (userAccount != null) {
           // postDetails.setUser_full_name(userAccount.news_site_name);
            postDetails.setEmail(userAccount.getEmail());
        }
        if (contentUri != null) {
            String finalThumbImgFile = thumbImgFile;
            ImageUploadHelper.getInstance().uploadImage(CreateNewsPostActivity.this, contentUri, outputFile, new ImageUploadListener() {
                @Override
                public void onImageUploaded(Object response) {
                    cancelProgressDialog();
                    postDetails.setPost_image(response.toString());
                    if (ATTACHMENT_MODE == VIDEO) saveTHumbnail(postDetails, finalThumbImgFile);
                    else {
                        if (Gallary_type.equalsIgnoreCase("video"))
                            saveTHumbnail(postDetails, finalThumbImgFile);
                        else {
                              if (!getIntent().getBooleanExtra("edit", false))
                                  saveHomePost(postDetails);
                              else upatePost(postDetails);
                        }

                    }

                }

                @Override
                public void onImageUploadFailed(String error) {
                    cancelProgressDialog();
                }
            });
        } else {
            postDetails.setPost_image("");
            saveHomePost(postDetails);
            cancelProgressDialog();
        }

    }

    public void saveTHumbnail(PostDetails postDetails, String outputFile) {
        showProgressDialog("", "Please wait...");
        ImageUploadHelper.getInstance().uploadImage(CreateNewsPostActivity.this, thumbcontentUri, outputFile, new ImageUploadListener() {
            @Override
            public void onImageUploaded(Object response) {
                cancelProgressDialog();
              //  postDetails.thumb_path = response.toString();

                 if (!getIntent().getBooleanExtra("edit", false))
                    saveHomePost(postDetails);
                 else upatePost(postDetails);
            }

            @Override
            public void onImageUploadFailed(String error) {
                cancelProgressDialog();
            }
        });

    }

    private void upatePost(PostDetails postDetails) {
        HashMap<String, Object> updatedValue = new HashMap<>();
        String outputFile = "";
        updatedValue.put("description", feedDesc.getText().toString().trim());
        updatedValue.put("is_adult_content", isAdultPost.isChecked());
        updatedValue.put("interests", selectedTopicList);

        if (savedUri == null) {
            if (ATTACHMENT_MODE != 0) {
                if (ATTACHMENT_MODE == IMAGE) {
                    updatedValue.put("source_type", "image");
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png";
                } else if (ATTACHMENT_MODE == VIDEO) {
                    updatedValue.put("source_path", "video");
                    //updatedValue.put("thumb_path", postDetails.thumb_path);
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".mp4";
                }

                if (contentUri != null) {
                    showProgressDialog(null, null);
                    ImageUploadHelper.getInstance().uploadImage(CreateNewsPostActivity.this, contentUri, outputFile, new ImageUploadListener() {
                        @Override
                        public void onImageUploaded(Object response) {
                            cancelProgressDialog();
                            updatedValue.put("source_path", response.toString());
                        }

                        @Override
                        public void onImageUploadFailed(String error) {
                            cancelProgressDialog();
                        }
                    });
                }
            }
        } else {
            postDetails.setPost_image(postDetails.getPost_image());
            update(updatedValue);
        }
    }


    private void saveHomePost(PostDetails postDetails) {
        showProgressDialog(null, null);
        BackendServer.getInstance().createHomePost(postDetails, new ResponseReceiver() {
            @Override
            public void onSuccess(int code, Object result) {
                cancelProgressDialog();
                Toast.makeText(getActivity(), "Post created successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                cancelProgressDialog();
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void update(Map<String, Object> updatedValue) {
        updatedValue.put("updated_at", ServerValue.TIMESTAMP);
        showProgressDialog(null, null);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("POSTS_LISTS").child(data.getRow_key()).updateChildren(updatedValue, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                cancelProgressDialog();
                onBackPressed();
            }
        });
    }


}
