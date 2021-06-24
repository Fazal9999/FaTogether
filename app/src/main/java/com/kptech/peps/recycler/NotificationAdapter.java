package com.kptech.peps.recycler;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.R;
import com.kptech.peps.activity.MessageDetailsActivity;
import com.kptech.peps.model.Chat;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.view.View.GONE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private Context mContext;
    private List<UserAccount> mUsers;
    private boolean ischat;
    private UserAccount userAccount;
    private String post_type;
    //    TextView last_msg;
    String theLastMessage, date_time;

    private static final String HOME_TYPE = "homeFeed";
    private static final String WORLD_WIDE_TYPE = "worldwideFeed";
    private static final String NEWS_TYPE = "newsAccountFeed";
    private static final String CONTENT_TYPE = "contentFeed";
    private static final String PODCAST_TYPE = "podcastFeed";


    public NotificationAdapter(Context mContext, List<UserAccount> mUsers, boolean ischat, String post_type){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat= ischat;
        this.post_type = post_type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_message, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserAccount user = mUsers.get(position);

        holder.username.setText(user.getUser_id());

//        if (post_type.equalsIgnoreCase(WORLD_WIDE_TYPE)){
//            Picasso.with(mContext).load(user.getImage_ur2()).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.profile_image);
//
//        }else if (post_type.equalsIgnoreCase(NEWS_TYPE)){
//            Picasso.with(mContext).load(user.getImage_ur3()).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.profile_image);
//
//        }
//        else if (post_type.equalsIgnoreCase(CONTENT_TYPE)){
//            Picasso.with(mContext).load(user.getImage_ur4()).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.profile_image);
//
//        }
//        else if (post_type.equalsIgnoreCase(PODCAST_TYPE)){
//            Picasso.with(mContext).load(user.getImage_ur5()).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.profile_image);
//        }
//        else {
//            Picasso.with(mContext).load(user.getImage_url()).centerCrop().fit().error(R.drawable.user_demo_dp).into(holder.profile_image);
//        }


        if (ischat){
            lastMessage(user.getEmail().replaceAll("[-+.^:,@]",""), holder.date);
        }else {
            holder.date.setVisibility(GONE);
        }
//        if (ischat){
//            if(user.getStatus().equals("online")){
//                holder.img_on.setVisibility(View.VISIBLE);
//                holder.img_off.setVisibility(GONE);
//            }else{
//                holder.img_on.setVisibility(GONE);
//                holder.img_off.setVisibility(View.VISIBLE);
//            }
//        }else {
//            holder.img_off.setVisibility(GONE);
//            holder.img_on.setVisibility(GONE);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("user_key", user.getEmail());
                intent.putExtra("post_type", post_type);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username, date;
        public ImageView profile_image;
        public ImageView img_off, img_on;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.msg_username);
            profile_image = itemView.findViewById(R.id.msg_avatar);
            img_off = itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
            date = itemView.findViewById(R.id.msg_timestamp);
        }
    }

    // check last message
    private void lastMessage(String sender_id, TextView last_msg){
        theLastMessage = "default";
        userAccount = DataHolder.getInstance().getmCurrentUser();
        String user_id = userAccount.getEmail().replaceAll("[-+.^:,@]","");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(post_type);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(user_id) && chat.getSender().equals(sender_id) ||
                            chat.getReceiver().equals(sender_id) && chat.getSender().equals(user_id) ){
                        theLastMessage = chat.getMessage();
                        date_time = chat.getTime() + " " + chat.getDate();

                    }
                }

                last_msg.setText(date_time);

//                switch (theLastMessage) {
//                    case "default":
//                        last_msg.setText("No Message.");
//                        break;
//                    default:
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
