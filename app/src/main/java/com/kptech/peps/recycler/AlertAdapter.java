package com.kptech.peps.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kptech.peps.R;
import com.kptech.peps.model.Alert;
import com.kptech.peps.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyAlertViewHolder> implements View.OnCreateContextMenuListener {

    private Context mContext;
    private List<Alert> alertData = new ArrayList<>();

    public void setAlertList(List<Alert> alerts) {
        if (alerts != null) {
            alertData = alerts;
        } else {
            alertData = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public MyAlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_cell, parent, false);
        return new AlertAdapter.MyAlertViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyAlertViewHolder holder, int position) {
        Alert alert = alertData.get(position);

        if (alert != null) {

            if (alert.getAlertFromPic() != null && !alert.getAlertFromPic().equals("")) {
                Picasso.with(mContext).load(alert.getAlertFromPic()).centerCrop().fit().error(R.drawable.ic_user_pic).into(holder.alertPic);
            } else {
                holder.alertPic.setImageResource(R.drawable.ic_user_pic);
            }
            holder.alertTime.setText(Utils.howLongAgo(alert.getAlertTimeStamp()));
            holder.alertPreview.setText(alert.getAlertPreview());
            holder.alertUsername.setText(alert.getAlertFrom());

            if (alert.getAlertType().equals("comment")) {
                holder.alertType.setText("Commented on your post!");
            } else if (alert.getAlertType().equals("like")) {
                holder.alertType.setText("Liked on your post!");
            }
        }

    }

    @Override
    public int getItemCount() {
        if (alertData != null) {
            return alertData.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    class MyAlertViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView alertPic;
        public TextView alertUsername, alertType, alertPreview, alertTime;

        public MyAlertViewHolder(@NonNull View itemView) {
            super(itemView);
            alertPic = itemView.findViewById(R.id.alert_pic);
            alertUsername = itemView.findViewById(R.id.alert_username);
            alertType = itemView.findViewById(R.id.alert_type);
            alertPreview = itemView.findViewById(R.id.alert_preview);
            alertTime = itemView.findViewById(R.id.alert_time);
        }
    }
}
