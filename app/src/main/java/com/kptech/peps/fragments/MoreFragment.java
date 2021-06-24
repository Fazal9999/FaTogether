package com.kptech.peps.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kptech.peps.R;
import com.kptech.peps.activity.CreateContentNewsActivity;
import com.kptech.peps.activity.HelpActivity;
import com.kptech.peps.activity.NewsRequestActivity;
import com.kptech.peps.activity.LoginActivity;
import com.kptech.peps.activity.NotificationActivity;
import com.kptech.peps.activity.ProfileActivity;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.PostsDataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.PreferenceStorage;
import com.kptech.peps.utils.Utils;

import static com.kptech.peps.utils.PreferenceStorage.LOGIN_STATUS;

public class MoreFragment extends Fragment {
    private TextView tv_profile, tv_logout, tv_new_acc_stutus, tv_podcast, content_status, podcastLAy, tv_note, tv_help;

    public MoreFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        tv_help = view.findViewById(R.id.tv_help);
        tv_profile = view.findViewById(R.id.tv_profile);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_new_acc_stutus = view.findViewById(R.id.tv_new_acc_stutus);
        tv_podcast = view.findViewById(R.id.tv_podcast);
        content_status = view.findViewById(R.id.content_status);
        podcastLAy = view.findViewById(R.id.tv_new_podcast);
        tv_note = view.findViewById(R.id.tv_note);

        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();

        setClick();
        TextView newsRequest = view.findViewById(R.id.tv_news_request);
        TextView contentRequest = view.findViewById(R.id.tv_content_request);


        tv_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                intent.putExtra("post_type", "homeFeed");
                getActivity().startActivity(intent);
            }
        });

        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HelpActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    private void setClick() {
        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("type","home");
                startActivity(intent);
            }
        });
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoutConfrm();
            }
        });

    }

    private void logoutConfrm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to logout ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void logout() {
        PostsDataHolder.getInstance().clear();
        PreferenceStorage.saveBooleanPref(getActivity(), LOGIN_STATUS, false);

        UserAccount accnt = DataHolder.getInstance().getmCurrentUser();
        if (accnt != null) {
            String key = Utils.getKey(accnt.getEmail());

            BackendServer.getInstance().updateProfile(key, accnt, new ResponseReceiver() {
                @Override
                public void onSuccess(int code, Object result) {
                    UserAccount account = (UserAccount) result;
                    DataHolder.getInstance().setmCurrentUser(account);
                    Toast.makeText(getActivity(), "Successfully log out.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getActivity(), "Something is wrong", Toast.LENGTH_SHORT).show();

                }
            });

        }

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void isC0lor(int value, TextView mayankwith) {

        if (value == 2) {
            mayankwith.setTextColor(Color.parseColor("#54D545"));
            {
                mayankwith.setText("Approved");
            }
        } else if (value == 1) {
            mayankwith.setTextColor(Color.parseColor("#E43942"));
            mayankwith.setText("Submited");
        } else mayankwith.setVisibility(View.GONE);
    }
}
