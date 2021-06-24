package com.kptech.peps.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.kptech.peps.Notification.APIService;
import com.kptech.peps.Notification.Client;
import com.kptech.peps.R;
import com.kptech.peps.model.Conversation;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.LatestMessage;
import com.kptech.peps.model.Message;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.recycler.MessageAdapter;
import com.kptech.peps.server.firebase.FirebaseConstants;
import com.kptech.peps.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class MessageDetailsActivity extends AppBaseActivity  {

    DatabaseReference reference;
    Intent intent;
    ImageView send_but, upload_pic;
    EditText send_text;
    TextView messageTitle;
    MessageAdapter messageAdapter;
    List<Message> messages = new ArrayList<>();
    List<Conversation> userConvos = new ArrayList<>();
    List<Conversation> recipientConvos = new ArrayList<>();
    RecyclerView recyclerView;
    String otherUserId, date, current_user_id, convoId, otherUserName;
    UserAccount userAccount, recipientAccount;
    boolean newConversation, fromC2g;

    //add for notification
    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        checkIfSessionExpired();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        send_but = findViewById(R.id.btn_send);
        send_text = findViewById(R.id.text_send);
        upload_pic = findViewById(R.id.upload_msg_pic);
        messageTitle = findViewById(R.id.message_bar_title);

        intent = getIntent();
        convoId = intent.getStringExtra("convo_id");
        fromC2g = intent.getBooleanExtra("from_c2g",false);

        otherUserId = intent.getStringExtra("other_user_key");
        getRecipient(otherUserId);

        otherUserName = intent.getStringExtra("other_user_name");
        messageTitle.setText(otherUserName);

        userAccount = DataHolder.getInstance().getmCurrentUser();
        current_user_id = Utils.getKey(userAccount.getEmail());

        getUserConvos(current_user_id);

        if (convoId == null) {
            newConversation = true;
        } else {
            newConversation = false;
            getMessages(convoId);
        }

        send_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                //end
                date = Utils.getTodayString(MessageDetailsActivity.this);
                String msg = send_text.getText().toString();

                if(!msg.equals("")){
                    sendMessage(msg, date);
                } else{
                    Toast.makeText(MessageDetailsActivity.this, "You can't send empty message.", Toast.LENGTH_LONG).show();
                }

                send_text.setText("");
                date = Utils.getTodayString(MessageDetailsActivity.this);
            }
        });
    }

    private void getMessages(String messageId){
        messages.clear();
        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.MESSAGES)
                .child(messageId)
                .child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot: snapshot.getChildren()) {
                    Message mMsg = childSnapshot.getValue(Message.class);
                    messages.add(mMsg);
                }
                messageAdapter = new MessageAdapter(MessageDetailsActivity.this, messages);
                recyclerView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserConvos(String id){
        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS)
                .child(id)
                .child("conversations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot: snapshot.getChildren()) {
                    Conversation convo = childSnapshot.getValue(Conversation.class);
                    for(DataSnapshot child: childSnapshot.getChildren()) {
                        if(child.getKey().equals("latestMessage")) {
                            LatestMessage latestMsg = child.getValue(LatestMessage.class);
                            if (convo != null) {
                                convo.setLatest_message(latestMsg);
                                userConvos.add(convo);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRecipient(String id){
        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS)
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipientAccount = snapshot.getValue(UserAccount.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS)
                .child(id)
                .child("conversations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot: snapshot.getChildren()) {
                    Conversation convo = childSnapshot.getValue(Conversation.class);
                    for(DataSnapshot child: childSnapshot.getChildren()) {
                        if(child.getKey().equals("latestMessage")) {
                            LatestMessage latestMsg = child.getValue(LatestMessage.class);
                            if (convo != null) {
                                convo.setLatest_message(latestMsg);
                                recipientConvos.add(convo);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage (String message, String date){
        Message newMessage = new Message();
        String newMsgID = current_user_id + '_' + otherUserId;
        newMessage.setContent(message);
        newMessage.setDate(date);
        newMessage.setId(newMsgID);
        newMessage.setIs_read(true);
        newMessage.setSender_id(current_user_id);
        newMessage.setType("text");
        newMessage.setUser_pic(userAccount.getProfile_pic());
        newMessage.setUsername(userAccount.getUser_id());

        messages.add(newMessage);

        if (newConversation) {
            newConvo(newMessage);
        } else{
            addToOldConvo(convoId, newMessage);
        }

        //add for notification
        if (notify){
        }
        notify = false;
    }

    private void newConvo(Message firstMsg) {
        convoId = "conversation_" + firstMsg.getId();

        Conversation newConvo = new Conversation();
        Conversation newConvoRec = new Conversation();
        LatestMessage lastMsg = new LatestMessage();
        LatestMessage latestMsgRep = new LatestMessage();

        lastMsg.setIs_read(true);
        lastMsg.setDate(firstMsg.getDate());
        lastMsg.setMessage(firstMsg.getContent());
        lastMsg.setUser_pic(recipientAccount.getProfile_pic());

        latestMsgRep.setIs_read(false);
        latestMsgRep.setDate(firstMsg.getDate());
        latestMsgRep.setMessage(firstMsg.getContent());
        latestMsgRep.setUser_pic(userAccount.getProfile_pic());

        newConvo.setLatest_message(lastMsg);
        newConvo.setFromC2G(fromC2g);
        newConvo.setFromVerified(userAccount.getIsVerified());
        newConvo.setId(convoId);
        newConvo.setName(recipientAccount.getFirst_name());
        newConvo.setOther_user_id(otherUserId);

        newConvoRec.setLatest_message(latestMsgRep);
        newConvoRec.setFromC2G(fromC2g);
        newConvoRec.setFromVerified(recipientAccount.getIsVerified());
        newConvoRec.setId(convoId);
        newConvoRec.setName(userAccount.getFirst_name());
        newConvoRec.setOther_user_id(current_user_id);

        userConvos.add(0,newConvo);
        recipientConvos.add(0, newConvoRec);

        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS)
                .child(current_user_id)
                .child("conversations");
        reference.setValue(userConvos);

        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS)
                .child(otherUserId)
                .child("conversations");
        reference.setValue(recipientConvos);

        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.MESSAGES).child(convoId);
        reference.setValue(messages);
    }

    private void addToOldConvo(String id, Message msg){
        Conversation newConvo = new Conversation();
        Conversation newConvoRec = new Conversation();
        LatestMessage lastMsg = new LatestMessage();
        LatestMessage latestMsgRep = new LatestMessage();

        lastMsg.setIs_read(true);
        lastMsg.setDate(msg.getDate());
        lastMsg.setMessage(msg.getContent());
        lastMsg.setUser_pic(recipientAccount.getProfile_pic());

        latestMsgRep.setIs_read(false);
        latestMsgRep.setDate(msg.getDate());
        latestMsgRep.setMessage(msg.getContent());
        latestMsgRep.setUser_pic(userAccount.getProfile_pic());

        newConvo.setLatest_message(lastMsg);
        newConvo.setFromC2G(fromC2g);
        newConvo.setFromVerified(userAccount.getIsVerified());
        newConvo.setId(id);
        newConvo.setName(recipientAccount.getFirst_name());
        newConvo.setOther_user_id(otherUserId);

        newConvoRec.setLatest_message(latestMsgRep);
        newConvoRec.setFromC2G(fromC2g);
        newConvoRec.setFromVerified(recipientAccount.getIsVerified());
        newConvoRec.setId(id);
        newConvoRec.setName(userAccount.getFirst_name());
        newConvoRec.setOther_user_id(current_user_id);

        for (Conversation convo: userConvos) {
            if (convo.getId().equals(newConvo.getId())) {
                userConvos.remove(convo);
            }
        }

        for (Conversation convo: recipientConvos) {
            if (convo.getId().equals(newConvo.getId())) {
                recipientConvos.remove(convo);
            }
        }
        userConvos.add(0,newConvo);
        recipientConvos.add(0, newConvoRec);

        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS)
                .child(current_user_id)
                .child("conversations");
        reference.setValue(userConvos);

        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USERS)
                .child(otherUserId)
                .child("conversations");
        reference.setValue(recipientConvos);

        reference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.MESSAGES)
                .child(id)
                .child("messages");
        reference.setValue(messages);
    }
}
