package com.kptech.peps.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.kptech.peps.R;
import com.kptech.peps.fragments.UserDetailFragment;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.utils.Utils;

public class UserdetailActivity extends AppBaseActivity  {

    boolean fromC2G = false;
    Fragment newFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_userdetail);

        Intent intent = getIntent();
        String user_key = intent.getStringExtra("user_key");

        if (user_key != null ) {
            newFragment = new UserDetailFragment(user_key, fromC2G, true);
        } else {
            String email=DataHolder.getInstance().getmCurrentUser().getEmail();
            if (email!=null){
                newFragment = new UserDetailFragment(Utils.getKey(email), fromC2G, false);
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.userDetailFragmentContainer, newFragment);
        transaction.commit();
    }
}
