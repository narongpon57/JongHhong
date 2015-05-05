package com.example.narongpon.jonghhong;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class JHService extends IntentService {

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "Service";

    public JHService(){
        super("JHService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {
            if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("message"), extras.getString("username"), extras.getString("status"));
            }
        }

        JHReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, String username, String status) {
        Log.e("Service", "GCM 123");
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, MainDrawer.class);
        if(status.equals("confirm")) {
            resultIntent.putExtra("pos","3");
        } else {
            resultIntent.putExtra("pos","3");

        }


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainDrawer.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.gcm_logo)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText("test");
        Log.e(TAG, msg);

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.e(TAG, "123");
    }
}
