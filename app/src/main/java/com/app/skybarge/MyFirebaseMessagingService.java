package com.app.skybarge;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.skybarge.activities.DashboardHome;
import com.app.skybarge.utils.AppUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by Hemanta on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static int BadgeCount=0;

    public static int value = 0;
    public static int PushNotificationId = -1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM", "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData() != null) {
            //  Log.d("FCM", "Notification Message Body: " + remoteMessage.getNotification().getBody());
            Log.e("body", "*" + remoteMessage.getData());
            BadgeCount = AppUtils.getBadgeCount(getApplicationContext());
            BadgeCount++;
            AppUtils.setBadgeCount(getApplicationContext(), BadgeCount);
            AppUtils.setBadge(getApplicationContext(), AppUtils.getBadgeCount(getApplicationContext()));
            sendNotification(remoteMessage.getData().get("text"), remoteMessage.getData().get("title"), remoteMessage.getData().get("type"));
//          sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTag());
        }
    }


    private void sendNotification(String messageBody, String title, String type) {
        PendingIntent pendingIntent;
        Log.e("title", "**" + title);
        Random r = new Random();
        int when = r.nextInt(1000);

        Intent intent2 = new Intent(this, DashboardHome.class);
        intent2.putExtra("type", type);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        pendingIntent = PendingIntent.getActivity(this, when, intent2,
                PendingIntent.FLAG_ONE_SHOT);

        int currentAPIVersion = Build.VERSION.SDK_INT;
        int icon = 0;
        if (currentAPIVersion == Build.VERSION_CODES.KITKAT) {
            icon = R.drawable.ic_launcher;

        } else if (currentAPIVersion >= Build.VERSION_CODES.LOLLIPOP) {
            icon = R.drawable.ic_launcher;
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(when, notificationBuilder.build());
    }

}