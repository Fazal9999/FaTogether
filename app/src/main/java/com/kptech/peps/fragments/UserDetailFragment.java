package com.kptech.peps.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.activity.MessageDetailsActivity;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.HomePostsAdapter;
import com.kptech.peps.recycler.MusicAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailFragment extends AppBaseFragment {

    private TextView user_name, c2g_interest, d_email, d_age, user_bio, user_message;
    ImageView back_button, settings_button, edit_button;
    private CircleImageView userProfilePic;
    private UserAccount userAccount;
    boolean mC2G;
    String user_key;
    boolean showBack;
    private Drawable mUserPlaceHolder;
    private UserAccount thisUser;
    private HomePostsAdapter mAdapter;
    DatabaseReference reference;
    private List<PostDetails> postDetailsList = new ArrayList<>();
    private List<PostDetails> postDetailsListfiltered = new ArrayList<>();

    public UserDetailFragment(String userKey, boolean c2g, boolean show_back) {
        user_key = userKey;
        mC2G = c2g;
        showBack = show_back;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_userdetail, container, false);
        userAccount = DataHolder.getInstance().getmCurrentUser();
        initViews(layout);
        if(!mC2G) {
            c2g_interest.setText(null);
            c2g_interest.setTextColor(getResources().getColor(R.color.white_color));
        }
        return layout;
    }

    private void initViews(View layout) {
        userProfilePic = layout.findViewById(R.id.profile_pfp);
        user_name = layout.findViewById(R.id.profile_username);
        back_button = layout.findViewById(R.id.back_profile);
        settings_button = layout.findViewById(R.id.profile_settings);
        RecyclerView mRecyclerView = layout.findViewById(R.id.recycler_view);
        d_email = layout.findViewById(R.id.d_email);
        d_age = layout.findViewById(R.id.d_age);
        //d_gender = layout.findViewById(R.id.d_gender);
        c2g_interest = layout.findViewById(R.id.profile_c2g);
        user_bio = layout.findViewById(R.id.user_bio);
        edit_button = layout.findViewById(R.id.user_edit_profile);
        user_message = layout.findViewById(R.id.user_message);

        mAdapter = new HomePostsAdapter();
        mAdapter.setmContext(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        fetchHomePostList();
        fetchUserData();
        mAdapter.setItemList(postDetailsListfiltered);
        mUserPlaceHolder = this.getResources().getDrawable(R.drawable.ic_user_pic);

        if (showBack) {
            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // CLOSE FRAGMENT HERE
                    getActivity().finish();
                }
            });
        } else {
            back_button.setVisibility(View.GONE);
        }

        user_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessageDetailsActivity.class);
                intent.putExtra("other_user_key", Utils.getKey(thisUser.getEmail()));
                intent.putExtra("other_user_name", thisUser.getFirst_name());
                intent.putExtra("from_c2g", mC2G);
                startActivity(intent);
            }
        });
    }

    private void fetchUserData(){
        reference = FirebaseDatabase.getInstance().getReference().child("USERS").child(user_key);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccount userAcct = dataSnapshot.getValue(UserAccount.class);

if(userAcct!=null){
   if (userAcct.lastName_public) {
                    d_email.setText("Name:     " + userAcct.getFirst_name() + " "+ userAcct.getLast_name());
                } else {
                    d_email.setText("Name:     " + userAcct.getFirst_name());
                }
    if (userAcct.dateOfBirth_public) {
        d_age.setVisibility(View.VISIBLE);
        String val = userAcct.getDate_of_birth();
        int mAge = 0;
        if (DataValidator.isValid(val)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            try {
                Date userDate = dateFormat.parse(val);
                if (userDate != null) {
                    Date currentDate = new Date();
                    int age = currentDate.getYear() - userDate.getYear();
                    if (age > 0 && (userDate.getMonth() > currentDate.getMonth())) {
                        age = age - 1;
                    }
                    mAge = age;
                }
            } catch (Exception e) {
                e.printStackTrace();
                mAge = 0;
            }
        }

        d_age.setText("Age:         " + mAge);
    }else{
        d_age.setText("Age:         Hidden");
        d_age.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
    }
    if (userAcct.getUser_bio().isEmpty() || userAcct.getUser_bio().equals("n/a")) {
        user_bio.setText("Blank");
        user_bio.setTextColor(getResources().getColor(R.color.white_color));
    } else {
        user_bio.setText(userAcct.getUser_bio());
    }

    if (userAcct.getEmail().equals(userAccount.getEmail())) {
        settings_button.setVisibility(View.VISIBLE);
        edit_button.setVisibility(View.VISIBLE);
        user_message.setVisibility(View.GONE);
    } else {
        user_message.setVisibility(View.VISIBLE);
        settings_button.setVisibility(View.GONE);
        edit_button.setVisibility(View.GONE);
    }

}






//                if (userAcct.gender_public) {
//                    d_gender.setVisibility(View.VISIBLE);
//                    d_gender.setText("Gender:   " + userAcct.getGender());
//                } else {
//                    d_gender.setText("Gender:   Hidden");
//                    d_gender.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchHomePostList() {
        showProgressDialog(null, null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.POSTS_LIST);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postDetailsList.clear();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    PostDetails info = childDataSnapshot.getValue(PostDetails.class);
                    if (info != null) {
                        postDetailsList.add(info);
                    }
                }

                postDetailsListfiltered.clear();
                for (PostDetails postDetails : postDetailsList) {
                    if (postDetails.getUser_ID().equals(user_key)){
                        postDetailsListfiltered.add(postDetails);
                    }
                }

                Collections.reverse(postDetailsListfiltered);
                mAdapter.setItemList(postDetailsListfiltered);
                mAdapter.notifyDataSetChanged();
                cancelProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cancelProgressDialog();
            }
        });

        DatabaseReference userRef = database.getReference("USERS").child(user_key);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisUser = dataSnapshot.getValue(UserAccount.class);
                mAdapter.setUserProfile(thisUser);
                mAdapter.notifyDataSetChanged();

                if (thisUser != null) {
                    if (thisUser.getProfile_pic() != null && !thisUser.getProfile_pic().equals("")) {
                        Picasso.with(getActivity()).load(thisUser.getProfile_pic()).centerCrop().fit().error(R.drawable.ic_user_pic).into(userProfilePic);
                    } else {
                        userProfilePic.setImageDrawable(mUserPlaceHolder);
                    }

                    String username = "@" + thisUser.getUser_id();

                    user_name.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
