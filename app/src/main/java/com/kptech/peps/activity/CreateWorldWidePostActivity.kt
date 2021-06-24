package com.kptech.peps.activity

import android.Manifest
import com.kptech.peps.customview.ExpertAreaCompletionView
import android.graphics.Bitmap
import com.kptech.peps.model.PostDetails
import android.app.TimePickerDialog
import android.os.Bundle
import com.kptech.peps.R
import com.google.gson.Gson
import android.text.InputType
import android.content.Intent
import com.kptech.peps.utils.DataValidator
import com.squareup.picasso.Picasso
import android.os.Build
import android.provider.MediaStore
import android.content.pm.PackageManager
import android.app.Dialog
import android.content.Context
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.kptech.peps.server.firebase.FirebaseConstants
import com.kptech.peps.server.firebase.image.ImageUploadHelper
import com.kptech.peps.server.firebase.image.ImageUploadListener
import com.kptech.peps.server.BackendServer
import com.kptech.peps.server.ResponseReceiver
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

import com.kptech.peps.model.DataHolder
import com.kptech.peps.utils.PathUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class CreateWorldWidePostActivity : AppBaseActivity() {
    lateinit var upload_lay: LinearLayout
    private val MY_REQ = 333
    private var selectedTopicList: ArrayList<String>? = ArrayList()
    private lateinit var interestTopicContainer: LinearLayout
    private lateinit var expertAreaCompletionView: ExpertAreaCompletionView
    var mUrl: EditText? = null
    private var parentLayout: LinearLayout? = null
    private val cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val storagePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val CAMERA_PERMISSION_REQ_CODE = 100
    private lateinit var createPost: TextView
    private val Gallary = 3
    private val VIDEO = 2
    private val IMAGE = 1
    private var ATTACHMENT_MODE = 0
    private var contentUri: Uri? = null
    private var thumbcontentUri: Uri? = null
    private var isAdultPost: CheckBox? = null
    private var feedDesc: EditText? = null
    var thumb: Bitmap? = null
    var img_placeholder: CircleImageView? = null
    var uploaded_img: ImageView? = null
    var uploaded_txt: TextView? = null
    var savedUri: Uri? = null
    var jcVideoPlayerStandard: VideoView? = null
    var play_button: ImageView? = null
    var data: PostDetails? = null
    lateinit var start_at: EditText
    lateinit var end_at: EditText
    var stat_timestamp = System.currentTimeMillis()
    var end_timestamp = System.currentTimeMillis()
    var picker: TimePickerDialog? = null
    var Gallary_type: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ww_create_post)
        mUrl = findViewById(R.id.tv_url)
        uploaded_img = findViewById(R.id.uploaded_img)
        img_placeholder = findViewById(R.id.img_placeholder)
        uploaded_txt = findViewById(R.id.uploaded_txt)
        play_button = findViewById(R.id.play_button)
        jcVideoPlayerStandard = findViewById(R.id.videoplayer)
        upload_lay = findViewById(R.id.upload_lay)
        expertAreaCompletionView = findViewById(R.id.interests)
        expertAreaCompletionView.allowDuplicates(false)
        expertAreaCompletionView.allowCollapse(false)
        expertAreaCompletionView.setClickable(false)
        parentLayout = findViewById(R.id.parent_layout)
        interestTopicContainer = findViewById(R.id.interest_container)
        createPost = findViewById(R.id.create_post)
        feedDesc = findViewById(R.id.feed_desc)
        isAdultPost = findViewById(R.id.check_adult)
        start_at = findViewById(R.id.start_at)
        end_at = findViewById(R.id.end_at)
        if (intent.getBooleanExtra("edit", false)) {
            setHeaderView("Edit WoldWide Post")
        } else setHeaderView("Create WoldWide Post")
        if (intent.getBooleanExtra("edit", false)) {
            data = Gson().fromJson(intent.getStringExtra("model"), PostDetails::class.java)
            start_at.setVisibility(View.GONE)
            end_at.setVisibility(View.GONE)
            setValue(data)
        }
        start_at.setInputType(InputType.TYPE_NULL)
        start_at.setOnClickListener(View.OnClickListener {
            val cldr = Calendar.getInstance()
            val hour = cldr[Calendar.HOUR_OF_DAY]
            val minutes = cldr[Calendar.MINUTE]
            // time picker dialog
            picker = TimePickerDialog(this@CreateWorldWidePostActivity,
                    { tp, sHour, sMinute ->
                        start_at.setText("$sHour hrs : $sMinute mins")
                        val calendar1 = Calendar.getInstance()
                        calendar1[Calendar.HOUR_OF_DAY] = sHour
                        calendar1[Calendar.MINUTE] = sMinute
                        stat_timestamp = calendar1.timeInMillis
                    }, hour, minutes, true)
            picker!!.show()
        })
        end_at.setInputType(InputType.TYPE_NULL)
        end_at.setOnClickListener(View.OnClickListener {
            val cldr = Calendar.getInstance()
            val hour = cldr[Calendar.HOUR_OF_DAY]
            val minutes = cldr[Calendar.MINUTE]
            // time picker dialog
            picker = TimePickerDialog(this@CreateWorldWidePostActivity,
                    { tp, sHour, sMinute ->
                        end_at.setText("$sHour hrs : $sMinute mins")
                        val calendar1 = Calendar.getInstance()
                        calendar1[Calendar.HOUR_OF_DAY] = sHour
                        calendar1[Calendar.MINUTE] = sMinute
                        end_timestamp = calendar1.timeInMillis
                    }, hour, minutes, true)
            picker!!.show()
        })
        interestTopicContainer.setOnClickListener(View.OnClickListener {
            val interestIntent = Intent(this@CreateWorldWidePostActivity, SelectInterestActivity::class.java)
            interestIntent.putStringArrayListExtra("SelectedTopics", selectedTopicList)
            startActivityForResult(interestIntent, MY_REQ)
        })
         //upload_lay.setOnClickListener(v -> selectContent());
        upload_lay.setOnClickListener(View.OnClickListener { v: View? -> selectContent() })
        createPost.setOnClickListener(View.OnClickListener {
            if (selectedTopicList == null || selectedTopicList!!.size == 0) {
                Toast.makeText(this@CreateWorldWidePostActivity, "Please select interests!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (!intent.getBooleanExtra("edit", false)) createHomePost() else upatePost(data)
        })
    }

    private fun customMediaSelectionDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_custom)
        val tv_image = dialog.findViewById(R.id.tv_media) as TextView
        val tv_video = dialog.findViewById(R.id.tv_video) as TextView


        dialog.show()
    }

    private fun setValue(data: PostDetails?) {
        if (DataValidator.isValid(data!!.description)) {
            feedDesc!!.setText(data.description)
            feedDesc!!.setSelection(data.description.length)
        }
        isAdultPost!!.isChecked = data.isIs_mature_content
        img_placeholder!!.visibility = View.GONE
        uploaded_txt!!.visibility = View.VISIBLE
        uploaded_img!!.visibility = View.VISIBLE
        if (!DataValidator.isValid(data.post_image)) return
        savedUri = Uri.parse(data.post_image)
        /*

            jcVideoPlayerStandard.setVisibility(View.GONE);
            play_button.setVisibility(View.GONE);
            uploaded_img.setVisibility(View.VISIBLE);
            img_placeholder.setVisibility(View.GONE);
            uploaded_txt.setVisibility(View.GONE);
*/Picasso.with(this)
                .load(savedUri)
                .error(R.drawable.post_placeholder)
                .into(uploaded_img)
    }

    private fun selectContent() {
        val pictureDialog = AlertDialog.Builder(this@CreateWorldWidePostActivity)
        pictureDialog.setTitle("Select Media From")
        val pictureDialogItems = arrayOf(
                "Image",
                "Video",
                "Gallery"
        )
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> ATTACHMENT_MODE = IMAGE
                1 -> ATTACHMENT_MODE = VIDEO
                2 -> {
                    Log.d("gallary", "onClick: ")
                    ATTACHMENT_MODE = Gallary
                }
            }
            Log.d("gallary", ATTACHMENT_MODE.toString())
            requestForPermission(CAMERA_PERMISSION_REQ_CODE, cameraPermission)
        }
        pictureDialog.show()
    }

    private fun requestForPermission(requestCode: Int, permission: Array<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, requestCode)
        } else {
            if (ATTACHMENT_MODE == IMAGE) {
                imageIntent()
            } else if (ATTACHMENT_MODE == VIDEO) {
                videoIntent()
            } else if (ATTACHMENT_MODE == Gallary) {
                gallaryIntent()
            }
        }
    }

    private fun imageIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, IMAGE)
    }

    private fun videoIntent() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val chooserIntent = Intent.createChooser(takeVideoIntent, "Capture Video")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takeVideoIntent))
        startActivityForResult(chooserIntent, VIDEO)
    }

    private fun gallaryIntent() {
        val intent = Intent()
        intent.type = "image/* video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Gallary)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQ_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (ATTACHMENT_MODE == IMAGE) {
                    imageIntent()
                } else if (ATTACHMENT_MODE == VIDEO) {
                    videoIntent()
                } else if (ATTACHMENT_MODE == Gallary) {
                    gallaryIntent()
                }
            } else {
                if (!(ActivityCompat.shouldShowRequestPermissionRationale(this@CreateWorldWidePostActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this@CreateWorldWidePostActivity, Manifest.permission.CAMERA))) {
                    showSnackbar()
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MY_REQ -> if (data != null) {
                selectedTopicList = data.getStringArrayListExtra("selectedTopic")
                expertAreaCompletionView!!.clear()
                var i = 0
                while (i < selectedTopicList!!.size) {
                    expertAreaCompletionView!!.addObject(selectedTopicList!![i])
                    i++
                }
                if (selectedTopicList!!.size > 0) {
                    expertAreaCompletionView!!.visibility = View.VISIBLE
                } else {
                    expertAreaCompletionView!!.visibility = View.GONE
                }
            }
            VIDEO -> {
                if (resultCode == RESULT_OK) {
                    val uri = data!!.data
                    val filePath = PathUtils.getPath(activity, uri)
                    val file = File(filePath)
                    //                    long file_size = (file.length() / (1024 * 1024));
//                    if (file_size <= 5) {
                    contentUri = uri
                    savedUri = null
                    uploaded_img!!.visibility = View.GONE
                    img_placeholder!!.visibility = View.GONE
                    uploaded_txt!!.visibility = View.GONE
                    jcVideoPlayerStandard!!.visibility = View.VISIBLE
                    play_button!!.visibility = View.VISIBLE
                    thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
                    thumbcontentUri = getImageUri(this@CreateWorldWidePostActivity, thumb)
                    play_button!!.setOnClickListener {
                        play_button!!.visibility = View.GONE
                        jcVideoPlayerStandard!!.setVideoURI(contentUri)
                        jcVideoPlayerStandard!!.requestFocus()
                        jcVideoPlayerStandard!!.start()
                    }
                    jcVideoPlayerStandard!!.setOnCompletionListener { play_button!!.visibility = View.VISIBLE }
                } else {
                    Toast.makeText(this@CreateWorldWidePostActivity, "Size cant be more than 5 mb", Toast.LENGTH_LONG).show()
                }
                Log.d("Video_URI", contentUri.toString())
            }
            IMAGE -> {
                if (resultCode == RESULT_OK) {
                    val thumbnail = data!!.extras!!["data"] as Bitmap?
                    val uri = getImageUri(this@CreateWorldWidePostActivity, thumbnail)
                    val filePath = PathUtils.getPath(activity, uri)
                    val file = File(filePath)
                    val file_size = file.length() / (1024 * 1024)
                    //                    if (file_size <= 5) {
                    savedUri = null
                    contentUri = uri
                    uploaded_img!!.visibility = View.VISIBLE
                    jcVideoPlayerStandard!!.visibility = View.GONE
                    img_placeholder!!.visibility = View.GONE
                    uploaded_txt!!.visibility = View.GONE
                    uploaded_img!!.setImageURI(contentUri)
                } else {
                    Toast.makeText(this@CreateWorldWidePostActivity, "Size cant be more than 5 mb", Toast.LENGTH_LONG).show()
                }
                Log.d("IMAGE_URI", contentUri.toString())
            }
            Gallary -> if (resultCode == RESULT_OK) {
                val uri = data!!.data
                if (uri.toString().contains("image")) {
                    Gallary_type = "image"

                    //handle image
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (bitmap != null && uri != null) {
                        savedUri = null
                        contentUri = uri
                        uploaded_img!!.visibility = View.VISIBLE
                        jcVideoPlayerStandard!!.visibility = View.GONE
                        img_placeholder!!.visibility = View.GONE
                        uploaded_txt!!.visibility = View.GONE
                        uploaded_img!!.setImageURI(contentUri)
                    }
                } else if (uri.toString().contains("video")) {
                    Gallary_type = "video"
                    val filePath = PathUtils.getPath(activity, uri)
                    val file = File(filePath)
                    val file_size = file.length() / (1024 * 1024)
                    //                        if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                    contentUri = uri
                    savedUri = null
                    uploaded_img!!.visibility = View.GONE
                    img_placeholder!!.visibility = View.GONE
                    uploaded_txt!!.visibility = View.GONE
                    jcVideoPlayerStandard!!.visibility = View.VISIBLE
                    play_button!!.visibility = View.VISIBLE
                    thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
                    thumbcontentUri = getImageUri(activity, thumb)
                    play_button!!.setOnClickListener {
                        play_button!!.visibility = View.GONE
                        jcVideoPlayerStandard!!.setVideoURI(contentUri)
                        jcVideoPlayerStandard!!.requestFocus()
                        jcVideoPlayerStandard!!.start()
                    }
                    jcVideoPlayerStandard!!.setOnCompletionListener { play_button!!.visibility = View.VISIBLE }
                    //
//                        } else {
//                            Toast.makeText(CreateNewsPostActivity.this, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show();
//                        }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(activity.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun showSnackbar() {
        Snackbar.make(parentLayout!!, "Please allow permission", Snackbar.LENGTH_LONG)
                .setAction("ENABLE") { openAppSettingScreen(this@CreateWorldWidePostActivity) }
                .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
                .show()
    }

    private fun openAppSettingScreen(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    private fun createHomePost() {
        if (contentUri == null && feedDesc!!.text.toString() == "") {
            Toast.makeText(this@CreateWorldWidePostActivity, "Please Write something or upload Video/image ", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedTopicList!!.size == 0) {
            Toast.makeText(this, "Please select interest", Toast.LENGTH_SHORT).show()
            return
        }
        showProgressDialog("", "Uploading...")
        val userAccount = DataHolder.getInstance().getmCurrentUser()
        val postDetails = PostDetails()
        var outputFile = ""
        var thumbImgFile = ""
        postDetails.isIs_mature_content = isAdultPost!!.isChecked
        if (ATTACHMENT_MODE != 0) {
            if (ATTACHMENT_MODE == IMAGE) {
                //   postDetails.setSource_type("image");
                outputFile = "WORLDWIDEPOSTS/" + System.currentTimeMillis() + ".png"
            } else if (ATTACHMENT_MODE == VIDEO) {
                //   postDetails.setSource_type("video");
                outputFile = "WORLDWIDEPOSTS/" + System.currentTimeMillis() + ".mp4"
                thumbImgFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png"
            } else if (ATTACHMENT_MODE == Gallary) {
                if (Gallary_type.equals("image", ignoreCase = true)) {
                    //  postDetails.setSource_type("image");
                    outputFile = "WORLDWIDEPOSTS/" + System.currentTimeMillis() + ".png"
                } else {
                    //  postDetails.setSource_type("video");
                    outputFile = "WORLDWIDEPOSTS/" + System.currentTimeMillis() + ".mp4"
                    thumbImgFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png"
                }
            }
        }
        postDetails.user_ID = "worldwideFeed"
        if (userAccount != null) {
            // postDetails.setUser_full_name(userAccount.worldwide_user_id);
            postDetails.email = userAccount.email
            // postDetails.setUser_image_url(userAccount.getImage_ur2());
        }
        postDetails.description = feedDesc!!.text.toString().trim { it <= ' ' }
        //        postDetails.setStart_at(stat_timestamp);
//        postDetails.setEnd_at(end_timestamp);
//        postDetails.setUser_full_name();
        if (contentUri != null) {
            val finalThumbImgFile = thumbImgFile
            ImageUploadHelper.getInstance().uploadImage(this@CreateWorldWidePostActivity, contentUri, outputFile, object : ImageUploadListener {
                override fun onImageUploaded(response: Any) {
                    cancelProgressDialog()
                    postDetails.post_image = response.toString()
                    if (ATTACHMENT_MODE == VIDEO) saveTHumbnail(postDetails, finalThumbImgFile) else {
                        if (Gallary_type.equals("video", ignoreCase = true)) saveTHumbnail(postDetails, finalThumbImgFile) else {
                            if (!intent.getBooleanExtra("edit", false)) saveHomePost(postDetails) else upatePost(postDetails)
                        }
                    }
                }

                override fun onImageUploadFailed(error: String) {
                    cancelProgressDialog()
                }
            })
        } else {
            postDetails.post_image = ""
            saveHomePost(postDetails)
            cancelProgressDialog()
        }
    }

    fun saveTHumbnail(postDetails: PostDetails, outputFile: String?) {
        showProgressDialog("", "Please wait...")
        ImageUploadHelper.getInstance().uploadImage(this@CreateWorldWidePostActivity, thumbcontentUri, outputFile, object : ImageUploadListener {
            override fun onImageUploaded(response: Any) {
                cancelProgressDialog()
                // postDetails.thumb_path = response.toString();
                if (!intent.getBooleanExtra("edit", false)) saveHomePost(postDetails) else upatePost(postDetails)
            }

            override fun onImageUploadFailed(error: String) {
                cancelProgressDialog()
            }
        })
    }

    /*
    private void upateHomePost(PostDetails postDetails) {


        UserAccount userAccount = DataHolder.getInstance().getmCurrentUser();
        String outputFile = "";

        if (selectedTopicList != null) {
            postDetails.setInterests(selectedTopicList);
        }
        if (mUrl != null) {
            postDetails.setUrl(mUrl.getText().toString());
        }
        postDetails.setPost_type("worldwideFeed");
        if (userAccount != null) {
            postDetails.setUser_full_name(userAccount.getFirst_name());
            postDetails.setEmail(userAccount.getEmail());
        }
        postDetails.setIs_adult_content(isAdultPost.isChecked());
        postDetails.setDescription(feedDesc.getText().toString().trim());

        if (savedUri == null) {
            if (ATTACHMENT_MODE != 0) {
                if (ATTACHMENT_MODE == IMAGE) {
                    postDetails.setSource_type("image");
                    outputFile = "WORLDWIDEPOSTS/" + System.currentTimeMillis() + ".png";
                } else if (ATTACHMENT_MODE == VIDEO) {
                    postDetails.setSource_type("video");
                    outputFile = "WORLDWIDEPOSTS/" + System.currentTimeMillis() + ".mp4";
                }
            }

            if (contentUri != null) {
                showProgressDialog(null, null);
                ImageUploadHelper.getInstance().uploadImage(CreateWorldWidePostActivity.this, contentUri, outputFile, new ImageUploadListener() {
                    @Override
                    public void onImageUploaded(Object response) {
                        cancelProgressDialog();
                        postDetails.setSource_path(response.toString());

                        update(postDetails);
                    }

                    @Override
                    public void onImageUploadFailed(String error) {
                        cancelProgressDialog();
                    }
                });
            }
        } else {
            postDetails.setSource_path(postDetails.getSource_path());
            update(postDetails);
        }


    }
*/
    private fun saveHomePost(postDetails: PostDetails) {
        showProgressDialog(null, null)
        BackendServer.getInstance().createHomePost(postDetails, object : ResponseReceiver {
            override fun onSuccess(code: Int, result: Any) {
                cancelProgressDialog()
                Toast.makeText(activity, "Post created successfully", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onError(error: String) {
                cancelProgressDialog()
                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun upatePost(postDetails: PostDetails?) {
        val updatedValue = HashMap<String, Any?>()
        var outputFile = ""
        updatedValue["description"] = feedDesc!!.text.toString().trim { it <= ' ' }
        updatedValue["is_adult_content"] = isAdultPost!!.isChecked
        updatedValue["interests"] = selectedTopicList
        if (savedUri == null) {
            if (ATTACHMENT_MODE != 0) {
                if (ATTACHMENT_MODE == IMAGE) {
                    updatedValue["source_type"] = "image"
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png"
                } else if (ATTACHMENT_MODE == VIDEO) {
                    updatedValue["source_path"] = "video"
                    // updatedValue.put("thumb_path", postDetails.thumb_path);
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".mp4"
                }
                if (contentUri != null) {
                    showProgressDialog(null, null)
                    ImageUploadHelper.getInstance().uploadImage(this@CreateWorldWidePostActivity, contentUri, outputFile, object : ImageUploadListener {
                        override fun onImageUploaded(response: Any) {
                            cancelProgressDialog()
                            updatedValue["source_path"] = response.toString()
                        }

                        override fun onImageUploadFailed(error: String) {
                            cancelProgressDialog()
                        }
                    })
                }
            }
        } else {
            postDetails!!.post_image = postDetails.post_image
            update(updatedValue)
        }
    }

    fun update(updatedValue: MutableMap<String, Any?>) {
        updatedValue["updated_at"] = ServerValue.TIMESTAMP
        showProgressDialog(null, null)
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("POSTS_LISTS").child(data!!.row_key).updateChildren(updatedValue) { databaseError, databaseReference ->
            cancelProgressDialog()
            onBackPressed()
        }
    }
}