package com.example.narongpon.jonghhong;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
                Log.e("extras",extras.toString());
                sendNotification(extras.getString("message"), extras.getString("username"), extras.getString("status"));

            }
        }

        JHReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, String username, String status) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences sp = getSharedPreferences("Jonghhong",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Intent resultIntent = new Intent(this, MainDrawer.class);
        if(status.equals("resv")) {
            editor.putInt("pos",5);
        } else {
            editor.putInt("pos",4);
        }
        editor.commit();

        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.gcm_logo)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        Log.e("test",msg);

        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setLights(Color.YELLOW, 3000, 3000);


        Log.e(TAG, msg);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setAutoCancel(true);

        String user = sp.getString("myID","");
        if(username.equals(user)) {

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else {
            Log.e(TAG, "UserID Not Match");
        }
    }
}
