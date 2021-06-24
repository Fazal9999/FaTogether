package com.kptech.peps.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kptech.peps.R;
import com.kptech.peps.fragments.AlertFragment;
import com.kptech.peps.fragments.AuthenticateFragment;
import com.kptech.peps.fragments.ComeGetherFragment;
import com.kptech.peps.fragments.HomeFragment;
import com.kptech.peps.fragments.MessageFragment;
import com.kptech.peps.fragments.MusicFragment;
import com.kptech.peps.fragments.MyPostFragment;
import com.kptech.peps.fragments.UserDetailFragment;
import com.kptech.peps.fragments.WorldwideFragment;
import com.kptech.peps.model.C2GUser;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.DataValidator;
import com.kptech.peps.utils.Utils;
import com.kptech.peps.Editor.VideoEditFragment;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppBaseActivity {
    private static final String TAG = HomeActivity.class.getName();
    private int mCurrentSelectedOption = -1;
    private int mOldSelectedOption = 10;
    private UserAccount userAccount;
    private ImageView tabAlert, tabMusic, tabCreate, tabMessage, tabProfile, navHome, navSearch, navBack, navNext, navFilter;
    private TextView alertText, musicText, createText, messageText, profileText;
    Fragment newFragment = null;
    FrameLayout tabFragment, navFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userAccount = DataHolder.getInstance().getmCurrentUser();
        initViews();
        launchNavFragments(mCurrentSelectedOption);
        tabFragment.setVisibility(View.GONE);
        navFragment.setVisibility(View.VISIBLE);
        Utils.preferences = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOldSelectedOption != 10) {
                    mCurrentSelectedOption = mOldSelectedOption;
                    mOldSelectedOption = 10;
                }

                if (mCurrentSelectedOption == -1) {
                    mCurrentSelectedOption = -2;
                    navHome.setImageResource(R.drawable.ic_world_nav);
                    launchNavFragments(mCurrentSelectedOption);
                } else if (mCurrentSelectedOption == -2) {
                    if (!userAccount.getIsC2G()) {
                        newC2GDialog dialog = new newC2GDialog(HomeActivity.this);
                        dialog.show();
                    } else {
                        mCurrentSelectedOption = -3;
                        navHome.setImageResource(R.drawable.ic_c2g_nav);
                        launchNavFragments(mCurrentSelectedOption);
                    }
                }
            }
        });

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOldSelectedOption != 10) {
                    mCurrentSelectedOption = mOldSelectedOption;
                    mOldSelectedOption = 10;
                }

                if (mCurrentSelectedOption == -3) {
                    mCurrentSelectedOption = -2;
                    navHome.setImageResource(R.drawable.ic_world_nav);
                    launchNavFragments(mCurrentSelectedOption);
                } else if (mCurrentSelectedOption == -2) {
                    mCurrentSelectedOption =  -1;
                    navHome.setImageResource(R.drawable.ic_home_nav);
                    launchNavFragments(mCurrentSelectedOption);
                }
            }
        });

        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        navFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSelectedOption == -3) {
                    Intent intent = new Intent(HomeActivity.this, Come2GetherFilterActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
                    startActivity(intent);
                }
            }
        });

        tabAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSelectedOption != 0 ) {
                    mOldSelectedOption = mCurrentSelectedOption;
                    launchTabFragments(0);
                    alertText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tabAlert.setImageResource(R.drawable.ic_alert_selected);
                } else {
                    detachTabFragment();
                    mCurrentSelectedOption = mOldSelectedOption;
                    launchNavFragments(mCurrentSelectedOption);
                    alertText.setTextColor(getResources().getColor(R.color.black));
                    tabAlert.setImageResource(R.drawable.ic_alert_tab);
                }
            }
        });

        tabMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogCustom);
                builder.setTitle("Music Coming Soon!");
                builder.setIcon(R.drawable.ic_music_selected);
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
//                launchTabFragments(1);
//                if (mCurrentSelectedOption != 1 ) {
//                    mOldSelectedOption = mCurrentSelectedOption;
//                    //launch dialog coming soon here
//                    musicText.setTextColor(getResources().getColor(R.color.colorPrimary));
//                    tabMusic.setImageResource(R.drawable.ic_music_selected);
//                } else {
//                    detachTabFragment();
//                    mCurrentSelectedOption = mOldSelectedOption;
//                launchNavFragments(mCurrentSelectedOption);
//                    musicText.setTextColor(getResources().getColor(R.color.black));
//                    tabMusic.setImageResource(R.drawable.ic_music_tab);
//                }
            }
        });

        tabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialogCustom);
                builder.setTitle("Set Up Create!");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();*/
                if (mCurrentSelectedOption != 2 ) {
                    mOldSelectedOption = mCurrentSelectedOption;
                    launchTabFragments(2);
                    createText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tabCreate.setImageResource(R.drawable.ic_create_selected);
                } else {
                    detachTabFragment();
                    mCurrentSelectedOption = mOldSelectedOption;
                    createText.setTextColor(getResources().getColor(R.color.black));
                    tabCreate.setImageResource(R.drawable.ic_create_tab);
                }
            }
        });

        tabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSelectedOption != 3 ) {
                    mOldSelectedOption = mCurrentSelectedOption;
                    launchTabFragments(3);
                    messageText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tabMessage.setImageResource(R.drawable.ic_message_selected);
                } else {
                    detachTabFragment();
                    mCurrentSelectedOption = mOldSelectedOption;
                    messageText.setTextColor(getResources().getColor(R.color.black));
                    tabMessage.setImageResource(R.drawable.ic_dm_tab);
                }
            }
        });

        tabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSelectedOption != 4 ) {
                    mOldSelectedOption = mCurrentSelectedOption;
                    launchTabFragments(4);
                    profileText.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tabProfile.setImageResource(R.drawable.ic_profile_selected);
                } else {
                    detachTabFragment();
                    mCurrentSelectedOption = mOldSelectedOption;
                    launchNavFragments(mCurrentSelectedOption);
                    profileText.setTextColor(getResources().getColor(R.color.black));
                    tabProfile.setImageResource(R.drawable.ic_profile_tab);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void initViews(){
        navSearch = findViewById(R.id.main_search);
        navFilter = findViewById(R.id.main_filter);
        navBack = findViewById(R.id.main_previous);
        navNext = findViewById(R.id.main_next);
        navHome = findViewById(R.id.main_home);
        navHome.setImageResource(R.drawable.ic_home_nav);
        tabFragment = findViewById(R.id.tabFragmentContainer);
        navFragment = findViewById(R.id.fragmentContainer);
        tabAlert = findViewById(R.id.tab_alert);
        tabMusic = findViewById(R.id.tab_music);
        tabCreate = findViewById(R.id.tab_create);
        tabMessage = findViewById(R.id.tab_message);
        tabProfile = findViewById(R.id.tab_profile);
        alertText = findViewById(R.id.alert_tv);
        musicText = findViewById(R.id.music_tv);
        createText = findViewById(R.id.create_tv);
        messageText = findViewById(R.id.message_tv);
        profileText = findViewById(R.id.profile_tv);
    }

    private void launchNavFragments(int position) {
        newFragment = null;
        tabFragment.setVisibility(View.GONE);
        navFragment.setVisibility(View.VISIBLE);

        if (-3 == position) {
            newFragment = new ComeGetherFragment();
        } else if (-2 == position) {
            if (userAccount.getIsSpecial_account()) {
                newFragment = new WorldwideFragment();
            } else {
                newFragment = new AuthenticateFragment();
            }
        } else if (-1 == position) {
            newFragment = new HomeFragment();
        }

        if (newFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.fragmentContainer, newFragment);
            transaction.commit();
            mCurrentSelectedOption = position;
        }
    }

    private void launchTabFragments(int position) {
       newFragment = null;
       tabFragment.setVisibility(View.VISIBLE);
       navFragment.setVisibility(View.GONE);

        if ((mCurrentSelectedOption != position)) {
            if (0 == position) {
                newFragment = new AlertFragment();
            } else if (1 == position) {
                newFragment = new MusicFragment();
            } else if (2 == position) {
                newFragment = new MyPostFragment();
            } else if (3 == position) {
                newFragment = new MessageFragment();
            } else if (4 == position) {
                newFragment = new UserDetailFragment(Utils.getKey(userAccount.getEmail()), false, false);
            }

            if (newFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.tabFragmentContainer, newFragment);
                transaction.commit();
                mCurrentSelectedOption = position;
            }
        }
    }

    private void detachTabFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.detach(newFragment);
        transaction.commit();
        tabFragment.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu) { //sign out
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mCurrentSelectedOption >= 0) {
//            launchTabFragments(mCurrentSelectedOption);
//        } else {
//            launchNavFragments(mCurrentSelectedOption);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class newC2GDialog extends Dialog {

        CircleImageView userTopPic;
        EditText new_firstName, new_age, new_gender, new_bio;
        Spinner new_interest;
        TextView new_confirm;
        Context thisContext;
        String[] hereFor = new String[]{ "", "Casual","Dating","MeetUps","Chatting" };
        String hereForSelected = "";

        public newC2GDialog(Context context) {
            super(context);
            setContentView(R.layout.custom_new_c2g);
            this.thisContext = context;
            userTopPic= findViewById(R.id.new_user_pic);
            new_firstName = findViewById(R.id.name_edit_view);
            new_age = findViewById(R.id.age_edit_view);
            new_gender = findViewById(R.id.gender_edit_view);
            new_interest = findViewById(R.id.interest_spinner);
            new_bio = findViewById(R.id.bio_edit_view);
            new_confirm = findViewById(R.id.dialog_confirm);

            new_interest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    hereForSelected = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(thisContext, R.layout.custom_spinner_item, hereFor);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            new_interest.setAdapter(dataAdapter);

            if (userAccount.getProfile_pic() != null && !userAccount.getProfile_pic().equals("")) {
                Picasso.with(thisContext).load(userAccount.getProfile_pic()).centerCrop().fit().error(R.drawable.ic_user_pic).into(userTopPic);
            } else {
                userTopPic.setImageResource(R.drawable.ic_user_pic);
            }

            new_firstName.setText(userAccount.getFirst_name());

            String val = userAccount.getDate_of_birth();
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

            new_age.setText(String.valueOf(mAge));

            new_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(new_firstName.length() < 2) {
                        Toast.makeText(thisContext, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    } else if (new_age.length() < 2) {
                        Toast.makeText(thisContext, "Please enter your age", Toast.LENGTH_SHORT).show();
                    } else if (new_gender.length() < 4) {
                        Toast.makeText(thisContext, "Please enter your gender", Toast.LENGTH_SHORT).show();
                    } else if (hereForSelected.equals("")) {
                        Toast.makeText(thisContext, "Please select your interest for C2G", Toast.LENGTH_SHORT).show();
                    } else if (new_bio.length() < 10){
                        Toast.makeText(thisContext, "Please enter a bio for your C2G Profile", Toast.LENGTH_SHORT).show();
                    } else {
                        showProgressDialog(null, "Creating Come2Gether Profile...");
                        userAccount.setIsC2G(true);
                        String key = Utils.getKey(userAccount.getEmail());
                        BackendServer.getInstance().updateProfile(key, userAccount, new ResponseReceiver() {
                            @Override
                            public void onSuccess(int code, Object result) {
                                cancelProgressDialog();
                                String c2gKey = "C2G" + key;
                                C2GUser newUser = new C2GUser();
                                newUser.setCtgAge(Integer.parseInt(new_age.getText().toString()));
                                newUser.setCtgBio(new_bio.getText().toString());
                                newUser.setCtgFirstName(new_firstName.getText().toString());
                                newUser.setCtgGender(new_gender.getText().toString());
                                newUser.setCtgHereFor(hereForSelected);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference(FirebaseConstants.COME2GETHER).child(c2gKey);
                                myRef.setValue(newUser);
                                mCurrentSelectedOption = -3;
                                launchNavFragments(mCurrentSelectedOption);
                            }

                            @Override
                            public void onError(String error) {
                                cancelProgressDialog();
                                Toast.makeText(HomeActivity.this, "Error updating  Come2Gether profile", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
        }
    }

}
