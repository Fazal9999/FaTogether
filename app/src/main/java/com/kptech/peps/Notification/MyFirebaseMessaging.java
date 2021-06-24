package com.kptech.peps.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kptech.peps.R;
import com.kptech.peps.activity.MessageDetailsActivity;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    private UserAccount userAccount;
    String current_user_id;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");

        userAccount = DataHolder.getInstance().getmCurrentUser();
        if (userAccount != null ){
            current_user_id = userAccount.getEmail().replaceAll("[-+.^:,@]","");
            if (sented.equals(current_user_id)){
                sendNotification(remoteMessage);
            }
        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        Log.d("sendNotification", "sendNotification: "+ remoteMessage.getData());

        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        NotificationManager noti_maneger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // create channels
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);
            noti_maneger.createNotificationChannel(mChannel);
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();

//        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));// j : notification_id

        Intent intent = new Intent(this, MessageDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_key", user);
        bundle.putString("post_type", remoteMessage.getData().get("post_type"));

        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap picture = BitmapFactory.decodeResource(getResources(), R.drawable.user_demo_dp);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(Integer.parseInt(icon))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(picture)///
                .setContentTitle(title)
                .setColor(Color.BLUE)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        int i = 0;
//        if (j > 0){
//            i = j;
//        }

        noti_maneger.notify(i, builder.build());
    }
}
