package com.kptech.peps.recycler

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View.OnCreateContextMenuListener
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.content.SharedPreferences
import com.kptech.peps.R
import com.squareup.picasso.Picasso
import com.kptech.peps.utils.DataValidator
import android.widget.SeekBar.OnSeekBarChangeListener
import android.content.Intent
import com.kptech.peps.activity.ImageViewActivity
import com.kptech.peps.activity.UserdetailActivity
import com.kptech.peps.server.BackendServer
import com.kptech.peps.activity.CommentsActivity
import android.provider.MediaStore
import android.view.ContextMenu.ContextMenuInfo
import androidx.appcompat.widget.AppCompatSeekBar
import android.media.MediaPlayer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kptech.peps.recycler.HomePostCommentsAdapter
import com.kptech.peps.utils.MusicUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.kptech.peps.activity.CreateHomePostActivity
import com.google.gson.Gson
import com.kptech.peps.recycler.HomePostsAdapter
import android.content.DialogInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.kptech.peps.Notification.Token
import com.kptech.peps.model.*
import com.kptech.peps.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * Created by suchandra on 12/3/2017.
 */
class HomePostsAdapter : RecyclerView.Adapter<HomePostsAdapter.MyViewHolder>(), OnCreateContextMenuListener {
    private var itemdata: List<PostDetails>? = ArrayList()
    private var userData: List<UserAccount> = ArrayList()
    private var commentData: Map<String, List<Comment>> = HashMap()
    private var mContext: Context? = null
    var alertDialog: AlertDialog? = null
    private var thumbcontentUri: Uri? = null
    private var userAccount: UserAccount? = null
    private var profileAccount: UserAccount? = null
    var thumb: Bitmap? = null
    private var otherAccount: UserAccount? = null
    private val database = FirebaseDatabase.getInstance()
    private var mUserPlaceHolder: Drawable? = null
    private var mPostPlaceHolder: Drawable? = null
    lateinit var unLikeImg: Drawable
    lateinit var likeImg: Drawable
    lateinit var likeList: MutableSet<String>
    lateinit var editor: SharedPreferences.Editor
    fun setItemList(data: List<PostDetails>?) {
        userAccount = DataHolder.getInstance().getmCurrentUser()
        itemdata = data ?: ArrayList()
    }

    fun setUserProfile(user: UserAccount?) {
        profileAccount = user ?: UserAccount()
    }

    fun setUserList(users: List<UserAccount>?) {
        userData = users ?: ArrayList()
    }

    fun setCommentList(comment: Map<String, List<Comment>>?) {
        commentData = comment
                ?: HashMap()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_detail_row, parent, false)
        likeList = Utils.preferences.getStringSet("likeList", null)!!
        unLikeImg = mContext!!.resources.getDrawable(R.drawable.ic_like_outline)
        likeImg = mContext!!.resources.getDrawable(R.drawable.ic_like_filled)
        unLikeImg.setBounds(0, 0, 60, 60)
        likeImg.setBounds(0, 0, 60, 60)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemdata!![position]
        otherAccount = UserAccount()
        if (userData.size > 0) {
            otherAccount = userData[position]
        }
        if (data.comments) {
            if (commentData.containsKey(data.row_key)) {
                for ((_, value) in commentData) {
                    holder.tv_comment.text = value.size.toString()
                }
            }
        } else {
            holder.tv_comment.text = "0"
        }
        if (data.post_likes != null) {
            holder.tv_like.text = data.post_likes.toString()
            if (likeList != null) {
                if (likeList!!.contains(data.row_key)) {
                    holder.tv_like.setCompoundDrawables(likeImg, null, null, null)
                }
            }
        } else {
            holder.tv_like.text = "0"
        }
        if (otherAccount != null) {
            if (otherAccount!!.first_name != null) {
                holder.name.text = otherAccount!!.first_name
            } else {
                holder.name.text = ""
            }
            if (otherAccount!!.getUser_id() != null) {
                holder.tv_username.text = otherAccount!!.getUser_id()
            } else {
                holder.tv_username.text = ""
            }
            if (otherAccount!!.profile_pic != null && otherAccount!!.profile_pic != "") {
                Picasso.with(mContext).load(otherAccount!!.profile_pic).centerCrop().fit().error(R.drawable.ic_user_pic).into(holder.groupImage)
            } else {
                holder.groupImage.setImageDrawable(mUserPlaceHolder)
            }
        }
        if (profileAccount != null) {
            if (profileAccount!!.first_name != null) {
                holder.name.text = profileAccount!!.first_name
            } else {
                holder.name.text = ""
            }
            if (profileAccount!!.getUser_id() != null) {
                holder.tv_username.text = profileAccount!!.getUser_id()
            } else {
                holder.tv_username.text = ""
            }
            if (profileAccount!!.profile_pic != null && profileAccount!!.profile_pic != "") {
                Picasso.with(mContext).load(profileAccount!!.profile_pic).centerCrop().fit().error(R.drawable.ic_user_pic).into(holder.groupImage)
            } else {
                holder.groupImage.setImageDrawable(mUserPlaceHolder)
            }
        }
        val `val` = data.description
        if (DataValidator.isValid(`val`)) {
            holder.group_desc.visibility = View.VISIBLE
            holder.group_desc.text = `val`
        } else {
            holder.group_desc.visibility = View.GONE
            holder.group_desc.text = ""
        }
        if (data.isIs_mature_content) {
            holder.group_mature.visibility = View.VISIBLE
        } else {
            holder.group_mature.visibility = View.GONE
        }
        try {
            holder.line1.text = Utils.howLongAgo(data.created_at)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val imageUrl = data.post_image
        val mp4 = ".mp4"
        val mp3 = ".mp3"
        val png = ".png"
        Log.i("realImageurlhomepost", imageUrl)
        if (DataValidator.isValid(imageUrl)) {
            if (imageUrl.toLowerCase(Locale.ROOT).contains(png.toLowerCase())) {
                Picasso.with(mContext).load(imageUrl).centerCrop().fit().into(holder.postImage)
            } else if (imageUrl.toLowerCase().contains(mp4.toLowerCase())) {
                holder.postImage.visibility = View.GONE
                holder.videoplayer.visibility = View.VISIBLE
                holder.playVideo.visibility = View.VISIBLE
                // Uri uri= Uri.fromFile(new File(imageUrl));
                val uri = Uri.parse(imageUrl)
                //thumb = ThumbnailUtils.createVideoThumbnail(uri.path!!, MediaStore.Video.Thumbnails.MINI_KIND);
                //thumbcontentUri = getImageUri(getmContext()!!, thumb!!);
                holder.playVideo.setOnClickListener {
                    holder.playVideo.visibility = View.GONE
                    holder.postImage.visibility = View.GONE
                    holder.videoplayer.setVideoURI(uri)
                    holder.videoplayer.requestFocus()
                    holder.videoplayer.start()
                }
            } else if (imageUrl.toLowerCase().contains(mp3.toLowerCase())) {
                holder.playVideo.visibility = View.GONE
                holder.postImage.visibility = View.GONE
                holder.postImage.visibility = View.GONE
                holder.videoplayer.visibility = View.GONE
                holder.playVideo.visibility = View.GONE
                holder.player_control_audio.visibility = View.VISIBLE
                holder.bt_play_audio.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        try {
                            holder.mp.setDataSource(imageUrl)
                            holder.mp.prepare()
                            holder.mp.start()
                            holder.seek_song_progressbar_audio.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
                                override fun onStartTrackingTouch(seekBar: SeekBar) {
                                    holder.mHandler.removeCallbacks(mUpdateTimeTask)
                                }

                                override fun onStopTrackingTouch(seekBar: SeekBar) {
                                    holder.mHandler.removeCallbacks(mUpdateTimeTask)
                                    val totalDuration = holder.mp.duration
                                    val currentPosition = holder.utils.progressToTimer(seekBar.progress, totalDuration)

                                    // forward or backward to certain seconds
                                    holder.mp.seekTo(currentPosition)

                                    // update timer progress again
                                    holder.mHandler.post(mUpdateTimeTask)
                                }
                            })
                            buttonPlayerAction()
                            // updateTimerAndSeekbar();
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    val mUpdateTimeTask: Runnable = object : Runnable {
                        override fun run() {
                            updateTimerAndSeekbar()
                        }

                        private fun updateTimerAndSeekbar() {
                            val totalDuration = holder.mp.duration
                            val currentDuration = holder.mp.duration

                            // Displaying Total Duration time
                            holder.tv_song_total_duration.text = holder.utils.milliSecondsToTimer(totalDuration.toLong())
                            // Displaying time completed playing
                            holder.tv_song_current_duration.text = holder.utils.milliSecondsToTimer(currentDuration.toLong())

                            // Updating progress bar
                            val progress = holder.utils.getProgressSeekBar(currentDuration.toLong(), totalDuration.toLong())
                            holder.seek_song_progressbar_audio.progress = progress
                        }
                    }

                    private fun buttonPlayerAction() {
                        holder.bt_play_audio.setOnClickListener {
                            if (holder.mp.isPlaying) {
                                holder.mp.pause()
                                // Changing button image to play button
                                holder.bt_play_audio.setImageResource(R.drawable.ic_play_arrow)
                            } else {
                                // Resume song
                                holder.mp.start()
                                // Changing button image to pause button
                                holder.bt_play_audio.setImageResource(R.drawable.ic_pause)
                                // Updating progress bar
                                holder.mHandler.post(mUpdateTimeTask)
                            }
                        }
                    }
                })
            }
        } else {
            holder.postImage.setImageDrawable(mPostPlaceHolder)
        }
        holder.postImage.setOnClickListener {
            val intent = Intent(mContext, ImageViewActivity::class.java)
            intent.putExtra("img", data.post_image)
            mContext!!.startActivity(intent)
        }

        // new add for details about user
        holder.details.setOnClickListener {
            val intent = Intent(mContext, UserdetailActivity::class.java)
            intent.putExtra("user_key", data.user_ID)
            mContext!!.startActivity(intent)
        }
        holder.postMore.setOnClickListener {
            if (data.email.equals(otherAccount!!.email, ignoreCase = true)) {
                showpopup_own(data)
            } else showpopup_other(data)
        }

        //set the comments part
        holder.tv_like.setOnClickListener {
            var likes = 0
            if (data.post_likes != null) {
                likes = data.post_likes
            }
            if (likeList != null) {
                if (likeList!!.contains(data.row_key)) {
                    // take away like
                    likes = likes - 1
                    likeList!!.remove(data.row_key)
                    holder.tv_like.setCompoundDrawables(unLikeImg, null, null, null)
                } else {
                    // heartadd like
                    likes = likes + 1
                    likeList!!.add(data.row_key)
                    holder.tv_like.setCompoundDrawables(likeImg, null, null, null)
                }
            } else {
                // add like
                likes = likes + 1
                likeList = HashSet()
                likeList.add(data.row_key)
                holder.tv_like.setCompoundDrawables(likeImg, null, null, null)
            }
            editor = Utils.preferences.edit()
            editor.putStringSet("likeList", likeList)
            editor.apply()
            data.post_likes = likes
            holder.tv_like.text = data.post_likes.toString()
            BackendServer.getInstance().postLike(data.row_key, likes)
        }
        holder.tv_comment.setOnClickListener {
            val intent = Intent(mContext, CommentsActivity::class.java)
            intent.putExtra("postModel", data.row_key)
            mContext!!.startActivity(intent)
        }
    }

    private fun getImageUri(getmContext: Context, thumb: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        // delete this if photos are bad
        thumb.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(getmContext.contentResolver, thumb, "Title", null)
        return Uri.parse(path)
    }

    override fun getItemCount(): Int {
        return if (itemdata != null) {
            itemdata!!.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getmContext(): Context? {
        return mContext
    }

    fun setmContext(mContext: Context) {
        this.mContext = mContext
        mUserPlaceHolder = mContext.resources.getDrawable(R.drawable.ic_user_pic)
        mPostPlaceHolder = mContext.resources.getDrawable(R.drawable.post_placeholder)
    }

    override fun onCreateContextMenu(contextMenu: ContextMenu, view: View, contextMenuInfo: ContextMenuInfo) {}
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var tv_username: TextView
        var line1: TextView
        var group_desc: TextView
        var showComments: TextView
        var groupImage: CircleImageView
        var postImage: ImageView
        var postComment: ImageView
        var postMenu: ImageView
        var mWriteComment: EditText
        var videoplayer: VideoView
        var seek_song_progressbar_audio: AppCompatSeekBar
        var playVideo: ImageView
        var mCommentsList: RecyclerView
        var player_control_audio: LinearLayout
        var mp = MediaPlayer()
        var tv_song_current_duration: TextView
        var tv_song_total_duration: TextView
        var bt_play_audio: FloatingActionButton
        var adapter: HomePostCommentsAdapter? = null
        val utils = MusicUtils()
        val mHandler = Handler()
        var postCommentsLayout: RelativeLayout
        var tv_like: TextView
        var tv_comment: TextView
        var postMore: TextView
        var group_mature: TextView
        var details: RelativeLayout

        init {
            name = view.findViewById(R.id.group_name)
            line1 = view.findViewById(R.id.post_time)
            group_desc = view.findViewById(R.id.group_description)
            groupImage = view.findViewById(R.id.group_icon_id)
            postImage = view.findViewById(R.id.group_post_image)
            videoplayer = view.findViewById(R.id.videoplayerview)
            playVideo = view.findViewById(R.id.play_button_video)
            postMenu = view.findViewById(R.id.post_menu_icon)
            showComments = view.findViewById(R.id.expand_comments)
            postCommentsLayout = view.findViewById(R.id.post_comment_layout)
            mWriteComment = view.findViewById(R.id.write_comment)
            postComment = view.findViewById(R.id.grp_post_comment)
            tv_like = view.findViewById(R.id.tv_like)
            tv_comment = view.findViewById(R.id.tv_comment)
            mCommentsList = view.findViewById(R.id.post_comment_list)
            val mLayoutManager = LinearLayoutManager(mContext)
            mCommentsList.layoutManager = mLayoutManager
            details = view.findViewById(R.id.details)
            postMore = view.findViewById(R.id.post_more)
            group_mature = view.findViewById(R.id.group_mature)
            tv_username = view.findViewById(R.id.group_username)
            player_control_audio = view.findViewById(R.id.player_control_audio)
            tv_song_current_duration = view.findViewById(R.id.tv_song_current_duration)
            tv_song_total_duration = view.findViewById(R.id.tv_song_total_duration)
            bt_play_audio = view.findViewById(R.id.bt_play_audio)
            seek_song_progressbar_audio = view.findViewById(R.id.seek_song_progressbar_audio)
        }
    }

    fun showpopup_other(data: PostDetails) {
        val dialogBuilder = AlertDialog.Builder(getmContext()!!, R.style.CustomDialogNew)
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_frnd, null)
        val tvCancel = dialogView.findViewById<View>(R.id.tvCancel) as TextView
        val tvReoprtUser = dialogView.findViewById<View>(R.id.tv_report_user) as TextView
        val tvBlockUser = dialogView.findViewById<View>(R.id.tv_block_user) as TextView
        val tvOffensiveContent = dialogView.findViewById<View>(R.id.tv_offensive_content) as TextView
        tvReoprtUser.setOnClickListener { v: View? ->
            alertDialog!!.dismiss()
            val reportUserModel = ReportUserModel()
            val dataRef = FirebaseDatabase.getInstance().reference.child("REPORT_USER").child(Utils.getKey(userAccount!!.email))
            val key = dataRef.push().key
            reportUserModel.created_at = ServerValue.TIMESTAMP
            reportUserModel.updated_at = ServerValue.TIMESTAMP
            reportUserModel.email = data.email
            reportUserModel.rowKey = key
            reportUserModel.type = "POSTS_LISTS"
            dataRef.child((key)!!).setValue(reportUserModel).addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    dataRef.child((key)).child("reported_by_users").child(Utils.getKey(userAccount!!.email)).setValue(1, null)
                    Toast.makeText(mContext, "User reoprted", Toast.LENGTH_SHORT).show()
                } else {
                }
            })
        }
        tvOffensiveContent.setOnClickListener { v: View? ->
            alertDialog!!.dismiss()
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("POSTS_LISTS").child(data.row_key)
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var offensiveCount: Long = 1
                    if (dataSnapshot.hasChild("offensive_content")) {
                        val offensiveContent = dataSnapshot.child("offensive_content").value as String?
                        offensiveCount = offensiveContent!!.toLong() + 1
                    }
                    val database = FirebaseDatabase.getInstance()
                    var myRef = database.getReference("POSTS_LISTS").child(data.row_key)
                    myRef.child("offensive_content").setValue(offensiveCount.toString())
                    myRef = database.getReference("POSTS_LISTS").child(data.row_key).child("reported_by_users")
                    myRef.child(Utils.getKey(userAccount!!.email)).setValue(1, object : DatabaseReference.CompletionListener {
                        override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {}
                    })
                    Toast.makeText(mContext, "Content marked as offensive", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        tvBlockUser.setOnClickListener { v: View? ->
            alertDialog!!.dismiss()
            val dataRef = FirebaseDatabase.getInstance().reference.child("BLOCK_USER").child(Utils.getKey(userAccount!!.email))
            dataRef.child(Utils.getKey(data.email)).setValue(1, object : DatabaseReference.CompletionListener {
                override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
                    if (databaseError != null) {
                        Log.d("Peps", "Data could not be saved " + databaseError.message)
                    } else {
                        Toast.makeText(mContext, "User blocked successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        tvCancel.setOnClickListener { view12: View? -> alertDialog!!.dismiss() }
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog!!.setCancelable(true)
        val window = alertDialog!!.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        alertDialog!!.show()
    }

    fun showpopup_own(data: PostDetails) {
        val dialogBuilder = AlertDialog.Builder(mContext!!, R.style.CustomDialogNew)
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_user, null)
        val tvCancel = dialogView.findViewById<View>(R.id.tvCancel) as TextView
        val tvEdit = dialogView.findViewById<View>(R.id.tvEdit) as TextView
        val tvDelete = dialogView.findViewById<View>(R.id.tvDelete) as TextView
        tvEdit.setOnClickListener { v: View? ->
            alertDialog!!.dismiss()
            val intent = Intent(mContext, CreateHomePostActivity::class.java)
            intent.putExtra("model", Gson().toJson(data))
            intent.putExtra("edit", true)
            mContext!!.startActivity(intent)
        }
        tvDelete.setOnClickListener { v: View? ->
            alertDialog!!.dismiss()
            run {
                Log.d(TAG, "DEl menu")
                val myQuittingDialogBox = AlertDialog.Builder((mContext)!!) //set message, title, and icon
                        .setTitle("Alert")
                        .setMessage("Are you sure, you want to delete this post?")
                        .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, whichButton: Int) {
                                val ref = FirebaseDatabase.getInstance().reference
                                val applesQuery = ref.child("POSTS_LISTS").orderByChild("row_key").equalTo(data.row_key)
                                applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (appleSnapshot: DataSnapshot in dataSnapshot.children) {
                                            appleSnapshot.ref.removeValue()
                                            Toast.makeText(mContext, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException())
                                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show()
                                    }
                                })
                                dialog.dismiss()
                            }
                        })
                        .setNegativeButton("No", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                dialog.dismiss()
                            }
                        })
                        .create()
                myQuittingDialogBox.show()
            }
        }
        tvCancel.setOnClickListener { v: View? -> alertDialog!!.dismiss() }
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog!!.setCancelable(true)
        val window = alertDialog!!.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        alertDialog!!.show()
    }

    //add for Notification
    private fun updateToken(token: String) {
        val accnt = DataHolder.getInstance().getmCurrentUser()
        val current_user_id = accnt.email.replace("[-+.^:,@]".toRegex(), "")
        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token1 = Token(token)
        reference.child(current_user_id).setValue(token1)
    }

    companion object {
        private val TAG = HomePostsAdapter::class.java.name
    }
}