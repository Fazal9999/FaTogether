package com.kptech.peps.recycler;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kptech.peps.R;
import com.kptech.peps.activity.MessageDetailsActivity;
import com.kptech.peps.model.C2GUser;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class Come2GetherAdapter extends RecyclerView.Adapter<Come2GetherAdapter.MyC2GViewHolder> implements View.OnCreateContextMenuListener {

    private Context mContext;
    private UserAccount currentUser;
    private List<C2GUser> userData;

    public void setUserList(List<C2GUser> users) {
        if (users != null) {
            userData = users;
        } else {
            userData = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public MyC2GViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ctg_cell, parent, false);
        currentUser = DataHolder.getInstance().getmCurrentUser();
        return new Come2GetherAdapter.MyC2GViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyC2GViewHolder holder, int position) {
        C2GUser mC2GUser = userData.get(position);

        if (mC2GUser != null) {
            if (mC2GUser.getCtgProfilePic() != null && !mC2GUser.getCtgProfilePic().equals("")) {
                Picasso.with(mContext).load(mC2GUser.getCtgProfilePic()).centerCrop().fit().error(R.drawable.ic_user_default_icon).into(holder.ctgPFP);
            } else {
                holder.ctgPFP.setImageResource(R.drawable.ic_user_default_icon);
            }

            holder.ctg_first_name.setText(mC2GUser.getCtgFirstName());
            holder.ctg_gender.setText(mC2GUser.getCtgGender());
            holder.ctg_interest.setText(mC2GUser.getCtgHereFor());
            holder.ctg_age.setText(String.valueOf(mC2GUser.getCtgAge()));
        }

        holder.ctgPFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // users pop up profile when pfp clicked
                c2gDialogBox dialog = new c2gDialogBox(mContext, mC2GUser);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (userData != null) {
            return userData.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    class MyC2GViewHolder extends RecyclerView.ViewHolder {

        public ImageView ctgPFP;
        public TextView ctg_first_name, ctg_gender, ctg_interest, ctg_age;

        public MyC2GViewHolder(@NonNull View itemView) {
            super(itemView);
            ctgPFP = itemView.findViewById(R.id.ctg_pfp);
            ctg_first_name = itemView.findViewById(R.id.ctg_firstName);
            ctg_gender = itemView.findViewById(R.id.ctg_gender);
            ctg_interest = itemView.findViewById(R.id.ctg_interest);
            ctg_age = itemView.findViewById(R.id.ctg_age);
        }
    }

    class c2gDialogBox extends Dialog {

        public ImageView dialogPFP;
        public TextView dlg_first_name, dlg_gender, dlg_interest, dlg_age, dlg_button, dlg_bio;
        Context thisContext;

        public c2gDialogBox(Context context, C2GUser user) {
            super(context);
            setContentView(R.layout.custom_c2g_dialog);
            this.thisContext = context;
            dialogPFP =  findViewById(R.id.dialog_pic);
            dlg_first_name = findViewById(R.id.dialog_name);
            dlg_gender = findViewById(R.id.dialog_gender);
            dlg_interest = findViewById(R.id.dialog_interest);
            dlg_age = findViewById(R.id.dialog_age);
            dlg_button = findViewById(R.id.dialog_message);
            dlg_bio = findViewById(R.id.dialog_bio);

            if (user.getCtgProfilePic() != null && !user.getCtgProfilePic().equals("")) {
               Picasso.with(thisContext).load(user.getCtgProfilePic()).centerCrop().fit().error(R.drawable.ic_user_default_icon).into(dialogPFP);
            } else {
                dialogPFP.setImageResource(R.drawable.ic_user_default_icon);
            }
            String firstNameText = "Name: " + user.getCtgFirstName();
            String ageText = "Age: " + user.getCtgAge();
            String genderText = "Gender: " + user.getCtgGender();

            dlg_first_name.setText(firstNameText);
            dlg_gender.setText(genderText);
            dlg_interest.setText(user.getCtgHereFor());
            dlg_age.setText(ageText);
            dlg_bio.setText(user.getCtgBio());

            if (user.getCtgUserID().equals(Utils.getKey(currentUser.getEmail()))) {
                dlg_button.setVisibility(View.GONE);
            } else {
                dlg_button.setVisibility(View.VISIBLE);
            }

            dlg_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // GO TO MESSAGE SCREEN
                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                    intent.putExtra("other_user_key", user.getCtgUserID());
                    intent.putExtra("other_user_name", user.getCtgFirstName());
                    intent.putExtra("from_c2g", true);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }
}
