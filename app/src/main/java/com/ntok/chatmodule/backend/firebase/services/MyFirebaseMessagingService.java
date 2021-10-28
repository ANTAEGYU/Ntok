package com.ntok.chatmodule.backend.firebase.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ntok.chatmodule.R;
import com.ntok.chatmodule.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


/**
 * Created by Sonam on 11-05-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static int NOTIFICATION_ID = 0;
    private String message;
    private String title;
    private String sender_id;
    private boolean isGroup = false;
    private String receiver_id;
    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

    }

    private void handleDataMessage(Map<String, String> data) {

        message = data.get("body");
        title = data.get("title");
        sender_id = data.get("sender_id");
        receiver_id = data.get("receiver_id");
        if(data.get("group").equalsIgnoreCase("true")){
            isGroup =  true;
        }else if(data.get("group").equalsIgnoreCase("false")){
            isGroup =  false;
        }

//        receiver_id = data.get("reciverid");

        NOTIFICATION_ID++;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            notificationUtils = new NotificationUtils(this);
            Notification.Builder nb = notificationUtils
                    .getAndroidChannelNotification(title,message,sender_id,isGroup,receiver_id);
            notificationUtils.getManager().notify(102, nb.build());
        }else{
            showNotification();
        }

        // handleMessage(getApplicationContext());
    }

    public void showNotification() {
        int id = (int) (Math.random() * 100);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        ii.putExtra("sender_id",sender_id);
        ii.putExtra("receiver_id",receiver_id);
        ii.putExtra("is_group",isGroup);
        ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, ii,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle(title);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}