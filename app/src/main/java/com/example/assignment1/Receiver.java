package com.example.assignment1;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class Receiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 123;
    public void onCreate(Context context, Intent intent) {
        showNotification(context);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    private void showNotification(Context context) {
        Log.d("A", "showNotification: Received");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "101")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("File Saved Successfully")
                .setContentText("Your file has been saved successfully.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
