package com.kptech.peps.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.gson.Gson
import com.kptech.peps.Editor.StartActivity
import com.kptech.peps.Editor.VideoEditFragment
import com.kptech.peps.R
import com.kptech.peps.customview.ExpertAreaCompletionView
import com.kptech.peps.customview.MyEditText
import com.kptech.peps.customview.MyEditText.KeyBoardInputCallbackListener
import com.kptech.peps.deepAr.DeepArMainActivity
import com.kptech.peps.model.DataHolder
import com.kptech.peps.model.PostDetails
import com.kptech.peps.server.BackendServer
import com.kptech.peps.server.ResponseReceiver
import com.kptech.peps.server.firebase.FirebaseConstants
import com.kptech.peps.server.firebase.image.ImageUploadHelper
import com.kptech.peps.server.firebase.image.ImageUploadListener
import com.kptech.peps.utils.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*
import java.util.*


class CreateHomePostActivity : AppBaseActivity() {
    private lateinit var upload_lay: LinearLayout
    private var parentLayout: LinearLayout? = null
    private val MY_REQ = 333
    private lateinit var seek_song_progressbar: AppCompatSeekBar
    private var selectedTopicList: ArrayList<String>? = ArrayList()
    private lateinit var interestTopicContainer: LinearLayout
    private lateinit var expertAreaCompletionView: ExpertAreaCompletionView
    private var selectTopicTitle: TextView? = null
    private val cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val storagePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val CAMERA_PERMISSION_REQ_CODE = 100
    private lateinit var createPost: TextView
    private val GALLERY = 1
    private val VIDEO_GALLERY = 2
    private val AUDIO = 3
    private var ATTACHMENT_MODE = 0


    private var contentUri: Uri? = null
    private var thumbcontentUri: Uri? = null
    private lateinit var feedDesc: MyEditText


    // Handler to update UI timer, progress bar etc,.
    private val mHandler: Handler = Handler()
    private lateinit var utils: MusicUtils
    var img_placeholder: CircleImageView? = null
    lateinit var uploaded_img: ImageView
    var uploaded_txt: TextView? = null
    private var savedUri: Uri? = null
    var thumb: Bitmap? = null
    private var imageCapture=""
    private var videopath:String?=null
    private var audiopath:String?=null
    private  lateinit var outPutStream:String
    var jcVideoPlayerStandard: VideoView? = null


    private lateinit var player_control:LinearLayout
    private lateinit var bt_play:FloatingActionButton
    private lateinit var mp: MediaPlayer
    var play_button: ImageView? = null
    var data = PostDetails()
    var stat_timestamp: Long? = null
    var end_timestamp: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
utils=MusicUtils()

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        (toolbar.findViewById<View>(R.id.toolbar_title) as TextView).text = "Create Post"

        setSupportActionBar(toolbar)
        val supportActionBar = supportActionBar
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        toolbar.setNavigationOnClickListener { // getActivity().onBackPressed();
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        uploaded_img = findViewById(R.id.uploaded_img)
        player_control=findViewById(R.id.player_control)
        uploaded_txt = findViewById(R.id.uploaded_txt)
        img_placeholder = findViewById(R.id.img_placeholder)
        play_button = findViewById(R.id.play_button)
        jcVideoPlayerStandard = findViewById(R.id.videoplayer)
        upload_lay = findViewById(R.id.upload_lay)
        bt_play=findViewById(R.id.bt_play)
        expertAreaCompletionView = findViewById(R.id.interests)
        expertAreaCompletionView.allowDuplicates(false)
        expertAreaCompletionView.allowCollapse(false)
        expertAreaCompletionView.setClickable(false)
        parentLayout = findViewById(R.id.parent_layout)
        seek_song_progressbar=findViewById(R.id.seek_song_progressbar)
        interestTopicContainer = findViewById(R.id.interest_container)
        selectTopicTitle = findViewById(R.id.select_topic_title)
        createPost = findViewById(R.id.create_post)
        feedDesc = findViewById(R.id.feed_desc)

        mp = MediaPlayer()
        mp.setOnCompletionListener { // Changing button image to play button
            bt_play.setImageResource(R.drawable.ic_play_arrow)
        }
        feedDesc.setKeyBoardInputCallbackListener(KeyBoardInputCallbackListener { inputContentInfo, flags, opts -> Toast.makeText(this@CreateHomePostActivity, "Good", Toast.LENGTH_SHORT).show() })

        //isAdultPost = findViewById(R.id.check_adult);
        if (intent.getBooleanExtra("edit", false)) setHeaderView("Edit Home Post") else setHeaderView("Create Home Post")
        if (intent.getBooleanExtra("edit", false)) {
            data = Gson().fromJson(intent.getStringExtra("model"), PostDetails::class.java)
            setValue(data)
        }
        interestTopicContainer.setOnClickListener(View.OnClickListener { view: View? ->
            val interestIntent = Intent(this@CreateHomePostActivity, SelectInterestActivity::class.java)
            interestIntent.putStringArrayListExtra("SelectedTopics", selectedTopicList)
            startActivityForResult(interestIntent, MY_REQ)
        })
        upload_lay.setOnClickListener(View.OnClickListener { v: View? -> selectContent() })
        createPost.setOnClickListener(View.OnClickListener { if (!intent.getBooleanExtra("edit", false)) createHomePost() else upateHomePost(data) })
        if (intent.hasExtra(Const.VIDEO)){
            videopath=intent.getStringExtra(Const.VIDEO);
            Log.i("", videopath.toString())
           // val uri = Uri.parse(videopath)
            val uri=Uri.fromFile(File(videopath!!))
            val file = File(uri.path!!)
            val file_size = file.length() / (1024 * 1024)
            Toast.makeText(this, "Option For edit", Toast.LENGTH_LONG).show()
            if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                contentUri = uri
                ATTACHMENT_MODE = VIDEO_GALLERY
                Log.i("Mode",ATTACHMENT_MODE.toString())
                savedUri = null
                Log.i("uri",contentUri.toString())
                img_placeholder!!.visibility = View.GONE
                uploaded_txt!!.visibility = View.GONE
                uploaded_img!!.visibility = View.GONE
                jcVideoPlayerStandard!!.visibility = View.VISIBLE
                play_button!!.visibility = View.VISIBLE
                thumb = ThumbnailUtils.createVideoThumbnail(uri.path!!, MediaStore.Video.Thumbnails.MINI_KIND)
                thumbcontentUri = getImageUri(this@CreateHomePostActivity, thumb)
                play_button!!.setOnClickListener {
                    play_button!!.visibility = View.GONE
                    uploaded_img!!.visibility = View.GONE
                    jcVideoPlayerStandard!!.setVideoURI(contentUri)
                    jcVideoPlayerStandard!!.requestFocus()
                    jcVideoPlayerStandard!!.start()
                }
            }
            else{
                Toast.makeText(this, "Video Size should not be morethan 15 Mb", Toast.LENGTH_LONG).show()
            }





        }
        if (intent.hasExtra(Const.MP)){

            audiopath=intent.getStringExtra(Const.MP);
            Log.i("mppath", audiopath.toString())
            // val uri = Uri.parse(videopath)
            val uri=Uri.fromFile(File(audiopath!!))
            val file = File(uri.path!!)
            val file_size = file.length() / (1024 * 1024)
            if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                contentUri = uri
                Log.i("mppath", contentUri.toString())
                ATTACHMENT_MODE = AUDIO
                img_placeholder!!.visibility = View.GONE
                uploaded_txt!!.visibility = View.GONE
                player_control.visibility = View.VISIBLE
                uploaded_img!!.visibility = View.GONE
                jcVideoPlayerStandard!!.visibility = View.GONE
                play_button!!.visibility = View.GONE
                bt_play!!.visibility = View.VISIBLE
                play_button!!.visibility = View.GONE
                uploaded_img!!.visibility = View.GONE
           //     thumb = ThumbnailUtils.createVideoThumbnail(uri.path!!, MediaStore.Video.Thumbnails.MINI_KIND)
             //   thumbcontentUri = getImageUri(this@CreateHomePostActivity, thumb)
                bt_play!!.setOnClickListener {
                  //  try {
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
                        mp.setDataSource(audiopath)
                        mp.prepare()
                        mp.start()
                        seek_song_progressbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
                            override fun onStartTrackingTouch(seekBar: SeekBar) {
                                // remove message Handler from updating progress bar
                                mHandler.removeCallbacks(mUpdateTimeTask)
                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar) {
                                mHandler.removeCallbacks(mUpdateTimeTask)
                                val totalDuration = mp.duration
                                val currentPosition: Int = utils.progressToTimer(seekBar.progress, totalDuration)

                                // forward or backward to certain seconds
                                mp.seekTo(currentPosition)

                                // update timer progress again
                                mHandler.post(mUpdateTimeTask)
                            }
                        })
                        buttonPlayerAction()
                        updateTimerAndSeekbar()





                                //  } catch (e: Exception) {
                       // Toast.makeText(this, "Cannot load audio file", Toast.LENGTH_SHORT).show();
                    //}
                }
            }





        }
        if (intent.hasExtra(Const.IMAGE)){
            imageCapture= intent.getStringExtra(Const.IMAGE).toString()
            Log.i("deepImageCapture", imageCapture.toString())
            val selecteduri= Uri.fromFile(File(imageCapture))
            val selectedPhotoUri =selecteduri

            try {
                val thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri)
                Log.i("Thumbnail", thumbnail.toString())
                //mBinding.ivDishImage.setImageBitmap(thumbnail) // Set to the imageView.
                val uri = getImageUri(this@CreateHomePostActivity, thumbnail)
                val filePath = PathUtils.getPath(activity, uri)
                val file = File(filePath)
                val file_size = file.length() / (1024 * 1024)
                if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                    ATTACHMENT_MODE=GALLERY
                    Log.i("ATTACHMENT_MODE", ATTACHMENT_MODE.toString())
                    savedUri = null
                    contentUri = uri
                    jcVideoPlayerStandard!!.visibility = View.GONE
                    play_button!!.visibility = View.GONE
                    uploaded_img!!.visibility = View.VISIBLE
                    img_placeholder!!.visibility = View.GONE
                    uploaded_txt!!.visibility = View.GONE
                    uploaded_img!!.setImageURI(contentUri)
                    Log.i("IMAGE_URI", contentUri.toString())

                } else {
                    Toast.makeText(this@CreateHomePostActivity, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show()
                }

            }
            catch (e: Exception){
                e.printStackTrace()
                Log.i("loadImageError", e.message.toString())
                Toast.makeText(this@CreateHomePostActivity, "failed to load image", Toast.LENGTH_LONG).show()

            }

        }

    }

    private fun buttonPlayerAction() {

        bt_play.setOnClickListener {
            // check for already playing
            if (mp.isPlaying) {
                mp.pause()
                // Changing button image to play button
                bt_play.setImageResource(R.drawable.ic_play_arrow)
            } else {
                // Resume song
                mp.start()
                // Changing button image to pause button
                bt_play.setImageResource(R.drawable.ic_pause)
                // Updating progress bar
                mHandler.post(mUpdateTimeTask)
            }


        }
    }

    private fun updateTimerAndSeekbar() {
        val totalDuration = mp.duration.toLong()
        val currentDuration = mp.currentPosition.toLong()

        // Displaying Total Duration time
        (findViewById<TextView>(R.id.tv_song_total_duration)).setText(utils.milliSecondsToTimer(totalDuration))
        // Displaying time completed playing
        (findViewById<TextView>(R.id.tv_song_current_duration)).setText(utils.milliSecondsToTimer(currentDuration))

        // Updating progress bar
        val progress = utils.getProgressSeekBar(currentDuration, totalDuration) as Int
        seek_song_progressbar.progress = progress
    }
    private fun setValue(data: PostDetails) {
        if (DataValidator.isValid(data.description)) {
            feedDesc!!.setText(data.description)
            feedDesc!!.setSelection(data.description.length)
        }
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
       /* val pictureDialog = AlertDialog.Builder(this@CreateHomePostActivity)
        pictureDialog.setTitle("Select Image From")
        val pictureDialogItems = arrayOf(
                "Image",
                "Video")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> ATTACHMENT_MODE = IMAGE
                1 -> ATTACHMENT_MODE = VIDEO
            }
            requestForPermission(CAMERA_PERMISSION_REQ_CODE, cameraPermission)
        }
        pictureDialog.show()*/

           val dialog = Dialog(this)
           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
           dialog.setCancelable(false)
           dialog.setContentView(R.layout.dialog_custom)
           val tv_media = dialog.findViewById(R.id.tv_media) as TextView
           val tv_video = dialog.findViewById(R.id.tv_video) as TextView
        val tv_mask = dialog.findViewById(R.id.tv_mask) as TextView
        tv_mask.setOnClickListener {
            dialog.dismiss()
            openDeepAr()
        }


      /*  tv_video.setOnClickListener{
            dialog.dismiss()
            val pictureDialog = AlertDialog.Builder(this@CreateHomePostActivity)
            pictureDialog.setTitle("Select Video From")
            val pictureDialogItems = arrayOf(
                    "Gallery",
                    "Record"


            )
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> ATTACHMENT_MODE = VIDEO_GALLERY
                    1 -> ATTACHMENT_MODE = RECORD

                }
                Toast.makeText(applicationContext, pictureDialogItems[which] + " is clicked", Toast.LENGTH_SHORT).show()
                if (pictureDialogItems[which] == "Record") {
                    openRecordActivity()
                    Toast.makeText(applicationContext, "Ar fdfdf is clicked", Toast.LENGTH_SHORT).show()
                }
                 ATTACHMENT_MODE=VIDEO_GALLERY
                requestForPermission(CAMERA_PERMISSION_REQ_CODE, cameraPermission);
            }
            pictureDialog.show()
        }*/
        tv_media.setOnClickListener{
            dialog.dismiss()
            val pictureDialog = AlertDialog.Builder(this@CreateHomePostActivity)
            pictureDialog.setTitle("Select Media")
            val pictureDialogItems = arrayOf(
                    "Video",
                    "Image"
            )
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> ATTACHMENT_MODE = VIDEO_GALLERY
                    1 -> ATTACHMENT_MODE = GALLERY


                }
              //  Toast.makeText(applicationContext, pictureDialogItems[which] + " is clicked", Toast.LENGTH_SHORT).show()

                requestForPermission(CAMERA_PERMISSION_REQ_CODE, cameraPermission);
           }
            pictureDialog.show()
            dialog.dismiss()

        }

           dialog.show()

    }

    private fun requestForPermission(requestCode: Int, permission: Array<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, requestCode);
        } else {
            //Toast.makeText(applicationContext,  "${IMAGE_MODE} is clicked", Toast.LENGTH_SHORT).show()
            if (ATTACHMENT_MODE == GALLERY) {
                Toast.makeText(this, "Gallery open ${ATTACHMENT_MODE}", Toast.LENGTH_LONG).show()
                openGallery()
            }
            else if (ATTACHMENT_MODE==VIDEO_GALLERY){
                Toast.makeText(this, "Video Gallery open ${ATTACHMENT_MODE}", Toast.LENGTH_LONG).show()
                openVideoGallery()
            }
        /*    if (ATTACHMENT_MODE == CAMERA) {
                openCamera()
            } else if (ATTACHMENT_MODE == GALLERY) {
                openGallery()
            }
            else if (ATTACHMENT_MODE==DEEPAR){
                openDeepAr()
            }
            else if (ATTACHMENT_MODE==VIDEO_GALLERY){
                openVideoGallery()
            }
            else if(ATTACHMENT_MODE==RECORD){
                openRecordActivity()
            }*/


        }

            /*if (ATTACHMENT_MODE == IMAGE) {
                imageIntent()
            } else if (ATTACHMENT_MODE == VIDEO) {
                videoIntent()
            }*/

    }

    private fun openVideoGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_GALLERY)

    }

    fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like u have turned off your permissions").setPositiveButton("Go TO Setting")
        { _, _->
            try {
                val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri= Uri.fromParts("package", packageName, null)
                intent.data=uri
                startActivity(intent)
            }
            catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){ dialog, _->{
            dialog.dismiss()
        }
        }.show()
    }

    private fun openDeepAr() {
       val intent=Intent(this, DeepArMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openGallery() {
        val Galleryintent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        Galleryintent.type = "image/*"
        startActivityForResult(Galleryintent, GALLERY)
    }

   /* private fun openCamera() {
        val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }*/

   /* private fun imageIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, IMAGE)
    }*/

  /*  private fun videoIntent() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val chooserIntent = Intent.createChooser(takeVideoIntent, "Capture Video")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takeVideoIntent))
        startActivityForResult(chooserIntent, VIDEO)
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQ_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                /*  if (ATTACHMENT_MODE == CAMERA) {
                    openCamera()
                } else*/

                if (ATTACHMENT_MODE == GALLERY) {
                    openGallery()
                }
                /* else if (ATTACHMENT_MODE == DEEPAR) {
                    openDeepAr()
                } */
                else if (ATTACHMENT_MODE == VIDEO_GALLERY) {
                    openVideoGallery()
                }
                /*  else if (ATTACHMENT_MODE == RECORD) {
                    openRecordActivity()
                }*/
            } else {
                if (!(ActivityCompat.shouldShowRequestPermissionRationale(this@CreateHomePostActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this@CreateHomePostActivity, Manifest.permission.CAMERA))) {
                    showSnackbar()
                }
            }
        }
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode==MY_REQ){
                if (data != null) {
                    selectedTopicList = data.getStringArrayListExtra("selectedTopic")
                    expertAreaCompletionView.clear()
                    for (i in selectedTopicList!!.indices) {
                        expertAreaCompletionView.addObject(selectedTopicList!![i])
                    }
                    if (selectedTopicList!!.size > 0) {
                        expertAreaCompletionView.visibility = View.VISIBLE
                        selectTopicTitle!!.visibility = View.GONE
                    } else {
                        expertAreaCompletionView.visibility = View.GONE
                        selectTopicTitle!!.visibility = View.VISIBLE
                    }
                }
            }

           /* if (requestCode == CAMERA) {

                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap // Bitmap from camera
                    Log.i("Thumbnail", thumbnail.toString())
                    //mBinding.ivDishImage.setImageBitmap(thumbnail) // Set to the imageView.
                    val uri = getImageUri(this@CreateHomePostActivity, thumbnail)
                    val filePath = PathUtils.getPath(activity, uri)
                    val file = File(filePath)
                    val file_size = file.length() / (1024 * 1024)
                    if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                        savedUri = null
                        contentUri = uri
                        jcVideoPlayerStandard!!.visibility = View.GONE
                        play_button!!.visibility = View.GONE
                        uploaded_img!!.visibility = View.VISIBLE
                        img_placeholder!!.visibility = View.GONE
                        uploaded_txt!!.visibility = View.GONE
                        uploaded_img!!.setImageURI(contentUri)
                        Log.i("IMAGE_URI", contentUri.toString())

                    } else {
                        Toast.makeText(this@CreateHomePostActivity, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show()
                    }

                }
            }*/
            // TODO Step 3: Get the selected image from gallery. The selected will be in form of URI so set it to the Dish ImageView.
            // START
            else if (requestCode == GALLERY) {

                data?.let {
                    // Here we will get the select image URI.
                    val selectedPhotoUri = data.data

                    try {
                        val thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri)
                        Log.i("Thumbnail", thumbnail.toString())
                        //mBinding.ivDishImage.setImageBitmap(thumbnail) // Set to the imageView.
                        val uri = getImageUri(this@CreateHomePostActivity, thumbnail)
                        val filePath = PathUtils.getPath(activity, uri)
                        val file = File(filePath)
                        val file_size = file.length() / (1024 * 1024)
                        if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                            savedUri = null
                            contentUri = uri
                            jcVideoPlayerStandard!!.visibility = View.GONE
                            play_button!!.visibility = View.GONE
                            uploaded_img!!.visibility = View.VISIBLE
                            img_placeholder!!.visibility = View.GONE
                            uploaded_txt!!.visibility = View.GONE
                            uploaded_img!!.setImageURI(contentUri)
                            Log.i("IMAGE_URI", contentUri.toString())

                        } else {
                            Toast.makeText(this@CreateHomePostActivity, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show()
                        }

                    }
                    catch (e: Exception){
                        e.printStackTrace()
                    }



                    //mBinding.ivDishImage.setImageURI(selectedPhotoUri) // Set the selected image from GALLERY to imageView.




                }
            }
            else if (requestCode==VIDEO_GALLERY){
                data?.let {

                    val uri = data.data
                    val filePath = PathUtils.getPath(activity, uri)
                    val file = File(filePath)
                    val file_size = file.length() / (1024 * 1024)
                    Toast.makeText(this, "Option For edit", Toast.LENGTH_LONG).show()

                    if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                        contentUri = uri
                        savedUri = null
                        val builder = AlertDialog.Builder(this)
                        //set title for alert dialog
                        builder.setTitle(R.string.editVideo)
                        //set message for alert dialog
                        builder.setMessage(R.string.want_edit)
                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                        //performing positive action
                        builder.setPositiveButton("Yes"){ dialogInterface, which ->
                            Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()


val intent=Intent(this, StartActivity::class.java)

                          // intent.putExtra(Const.VIDEO, contentUri.toString());
                            startActivity(intent)


                        }
                        //performing cancel action
                       /* builder.setNeutralButton("Cancel"){dialogInterface , which ->
                            Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
                        }*/
                        //performing negative action
                        builder.setNegativeButton("No"){ dialogInterface, which ->
                            Toast.makeText(applicationContext, "clicked No", Toast.LENGTH_LONG).show()
                            img_placeholder!!.visibility = View.GONE
                            uploaded_txt!!.visibility = View.GONE
                            uploaded_img!!.visibility = View.GONE
                            jcVideoPlayerStandard!!.visibility = View.VISIBLE
                            play_button!!.visibility = View.VISIBLE
                            thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
                            thumbcontentUri = getImageUri(this@CreateHomePostActivity, thumb)
                            play_button!!.setOnClickListener {
                                play_button!!.visibility = View.GONE
                                uploaded_img!!.visibility = View.GONE
                                jcVideoPlayerStandard!!.setVideoURI(contentUri)
                                jcVideoPlayerStandard!!.requestFocus()
                                jcVideoPlayerStandard!!.start()
                                  }
                        // Create the AlertDialog

                        }
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)

                        alertDialog.show()

                        jcVideoPlayerStandard!!.setOnCompletionListener { play_button!!.visibility = View.VISIBLE }
                    } else {
                        Toast.makeText(this@CreateHomePostActivity, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show()
                    }
                }
                    }


            // END
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }


        /*  public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                    selectTopicTitle!!.visibility = View.GONE
                } else {
                    expertAreaCompletionView!!.visibility = View.GONE
                    selectTopicTitle!!.visibility = View.VISIBLE
                }
            }
           *//* VIDEO -> if (resultCode == RESULT_OK) {
                val uri = data!!.data
                val filePath = PathUtils.getPath(activity, uri)
                val file = File(filePath)
                val file_size = file.length() / (1024 * 1024)
                if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                    contentUri = uri
                    savedUri = null
                    img_placeholder!!.visibility = View.GONE
                    uploaded_txt!!.visibility = View.GONE
                    uploaded_img!!.visibility = View.GONE
                    jcVideoPlayerStandard!!.visibility = View.VISIBLE
                    play_button!!.visibility = View.VISIBLE
                    thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
                    thumbcontentUri = getImageUri(this@CreateHomePostActivity, thumb)
                    play_button!!.setOnClickListener {
                        play_button!!.visibility = View.GONE
                        uploaded_img!!.visibility = View.GONE
                        jcVideoPlayerStandard!!.setVideoURI(contentUri)
                        jcVideoPlayerStandard!!.requestFocus()
                        jcVideoPlayerStandard!!.start()
                    }
                    jcVideoPlayerStandard!!.setOnCompletionListener { play_button!!.visibility = View.VISIBLE }
                } else {
                    Toast.makeText(this@CreateHomePostActivity, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show()
                }
            }*//*
            CAMERA -> if (resultCode == RESULT_OK) {
                val thumbnail = data!!.extras!!["data"] as Bitmap?
                val uri = getImageUri(this@CreateHomePostActivity, thumbnail)
                val filePath = PathUtils.getPath(activity, uri)
                val file = File(filePath)
                val file_size = file.length() / (1024 * 1024)
                if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                    savedUri = null
                    contentUri = uri
                    jcVideoPlayerStandard!!.visibility = View.GONE
                    play_button!!.visibility = View.GONE
                    uploaded_img!!.visibility = View.VISIBLE
                    img_placeholder!!.visibility = View.GONE
                    uploaded_txt!!.visibility = View.GONE
                    uploaded_img!!.setImageURI(contentUri)
                    Log.d("IMAGE_URI", contentUri.toString())

                } else {
                    Toast.makeText(this@CreateHomePostActivity, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show()
                }
                Log.d("IMAGE_URI", contentUri.toString())
            }
          *//*  GALLERY -> if (resultCode == RESULT_OK) {
                val thumbnail = data!!.extras!!["data"] as Bitmap?
                val uri = getImageUri(this@CreateHomePostActivity, thumbnail)
                val filePath = PathUtils.getPath(activity, uri)
                val file = File(filePath)
                val file_size = file.length() / (1024 * 1024)
                if (file_size <= Constants.MAX_SIZE_ALLOWED) {
                    savedUri = null
                    contentUri = uri
                    jcVideoPlayerStandard!!.visibility = View.GONE
                    play_button!!.visibility = View.GONE
                    uploaded_img!!.visibility = View.VISIBLE
                    img_placeholder!!.visibility = View.GONE
                    uploaded_txt!!.visibility = View.GONE
                    uploaded_img!!.setImageURI(contentUri)
                } else {
                    Toast.makeText(this@CreateHomePostActivity, "Size cant be more than 15 mb", Toast.LENGTH_LONG).show()
                }
                Log.d("IMAGE_URI", contentUri.toString())
            }*//*

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
*/

    fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        // delete this if photos are bad
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(activity.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun showSnackbar() {
        Snackbar.make(parentLayout!!, "Please allow permission", Snackbar.LENGTH_LONG)
                .setAction("ENABLE") { openAppSettingScreen(this@CreateHomePostActivity) }
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
        if (contentUri == null) {
            Toast.makeText(this, "Please Upload media an image or video", Toast.LENGTH_SHORT).show()
            return
        }
        showProgressDialog("", "Uploading...")
        val userAccount = DataHolder.getInstance().getmCurrentUser()
        val postDetails = PostDetails()
        var outputFile = ""
        var thumbImgFile = ""
        postDetails.isIs_mature_content = false
        if (ATTACHMENT_MODE != 0) {
            if (ATTACHMENT_MODE == GALLERY) {
//                outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png";
                outputFile = "POSTS_LISTS/" + System.currentTimeMillis() + ".png"
            } else if (ATTACHMENT_MODE == VIDEO_GALLERY) {
                outputFile = "POSTS_LISTS/" + System.currentTimeMillis() + ".mp4"
                thumbImgFile = "POSTS_LISTS/" + System.currentTimeMillis() + ".png"
            }
            else if (ATTACHMENT_MODE == AUDIO) {
                outputFile = "POSTS_LISTS/" + System.currentTimeMillis() + ".mp3"
                thumbImgFile = "POSTS_LISTS/" + System.currentTimeMillis() + ".png"
            }
        }
        if (userAccount != null) {
            postDetails.email = userAccount.email
        }
        postDetails.description = feedDesc!!.text.toString().trim { it <= ' ' }
        if (contentUri != null) {
            val finalThumbImgFile = thumbImgFile
            ImageUploadHelper.getInstance().uploadImage(this@CreateHomePostActivity, contentUri, outputFile, object : ImageUploadListener {
                override fun onImageUploaded(response: Any) {
                    cancelProgressDialog()
                    postDetails.post_image = response.toString()
                    if (ATTACHMENT_MODE == VIDEO_GALLERY) saveTHumbnail(postDetails, finalThumbImgFile) else {
                        if (!intent.getBooleanExtra("edit", false)) saveHomePost(postDetails) else upateHomePost(postDetails)
                    }
                     if (ATTACHMENT_MODE == AUDIO) saveTHumbnail(postDetails, finalThumbImgFile) else {
                        if (!intent.getBooleanExtra("edit", false)) saveHomePost(postDetails) else upateHomePost(postDetails)
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
        ImageUploadHelper.getInstance().uploadImage(this@CreateHomePostActivity, thumbcontentUri, outputFile, object : ImageUploadListener {
            override fun onImageUploaded(response: Any) {
                cancelProgressDialog()
                //postDetails.thumb_path = response.toString();
                if (!intent.getBooleanExtra("edit", false)) saveHomePost(postDetails) else upateHomePost(postDetails)
            }

            override fun onImageUploadFailed(error: String) {
                cancelProgressDialog()
            }
        })
    }

    private fun upateHomePost(postDetails: PostDetails) {
        val updatedValue = HashMap<String, Any>()
        var outputFile = ""
        updatedValue["description"] = feedDesc!!.text.toString().trim { it <= ' ' }
        if (savedUri == null) {
            if (ATTACHMENT_MODE != 0) {
                if (ATTACHMENT_MODE == GALLERY) {
                    updatedValue["source_type"] = "image"
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".png"
                } else if (ATTACHMENT_MODE == VIDEO_GALLERY) {
                    updatedValue["source_path"] = "video"
                    //  updatedValue.put("thumb_path",postDetails.thumb_path);
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".mp4"
                }
                else if (ATTACHMENT_MODE == AUDIO) {
                    updatedValue["source_path"] = "audio"
                    //  updatedValue.put("thumb_path",postDetails.thumb_path);
                    outputFile = FirebaseConstants.POSTS_LIST + "/" + System.currentTimeMillis() + ".mp3"
                }
                if (contentUri != null) {
                    showProgressDialog(null, null)
                    ImageUploadHelper.getInstance().uploadImage(this@CreateHomePostActivity, contentUri, outputFile, object : ImageUploadListener {
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
            postDetails.post_image = postDetails.post_image
            update(updatedValue)
        }
    }

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

    fun update(updatedValue: MutableMap<String, Any>) {
        updatedValue["updated_at"] = ServerValue.TIMESTAMP
        showProgressDialog(null, null)
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("POSTS_LISTS").child(data.row_key).updateChildren(updatedValue) { databaseError, databaseReference ->
            cancelProgressDialog()
            onBackPressed()
        }
    }
    private val mUpdateTimeTask = Runnable { updateTimerAndSeekbar() }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = 67108864
        startActivity(intent)
        finish()
    }
}