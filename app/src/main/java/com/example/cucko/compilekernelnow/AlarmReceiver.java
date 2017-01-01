package com.example.cucko.compilekernelnow;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateUtils;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by cucko on 1/1/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    final static int NOTIFICATION_ID = 1;
    final int REQUEST_CODE = 1;

    private static Notification GetNotification(Context c) {
//        NotificationCompat.WearableExtender wearableExtender =
//                new NotificationCompat.WearableExtender()
//                        .setHintHideIcon(false);
////                        .setBackground(R.mipmap.ic_launcher);

//        Calendar cal = Calendar.getInstance();
        SharedPreferences prefs = c.getSharedPreferences("settings", 0);
        long lastCompile = prefs.getLong("lastCompile", 0);
//        Log.d("lastCompile", ""+DateUtils.getRelativeTimeSpanString(lastCompile));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.ic_notification)
//                .setColor(c.getResources().getColor(R.color.brandSantaTracker))
//                .setContentTitle("Have you compiled your kernel today?")
                .setContentTitle("Kernel not compiled for 24 hours")
//                .setContentText("Time: " + DateUtils.formatDateTime(c, cal.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME))
//                .setContentText("Last Compile: " + DateUtils.formatDateTime(c, lastCompile, DateUtils.FORMAT_ABBREV_RELATIVE))
                .setContentText("Last Compile: " + DateUtils.getRelativeTimeSpanString(lastCompile))
//                .setContentText("Open notification to compile it now")
                .setAutoCancel(true);
//                .extend(wearableExtender);

        // Add the type of notification for wearable to app tracking of clicks
        Intent i = new Intent(c, MainActivity.class);

        i.putExtra("notification", "1");
        // Add the intent to open the main startup activity when clicked
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(i);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        return mBuilder.build();
    }

    /**
     * Display a generic Santa notification with content loaded from a string resource.
     */
    public static void CreateNotification(Context c) {
        Notification n = GetNotification(c);

        //Post the notification.
        NotificationManagerCompat.from(c)
                .notify(NOTIFICATION_ID, n);
    }

    public void onReceive(Context context, Intent intent2) {

        CreateNotification(context);

        setAlarm(context);
    }

    private void setAlarm(Context c) {

        Intent intent = new Intent(c, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, REQUEST_CODE, intent, 0);

        AlarmManager alarmManager = (AlarmManager) c.getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 24 * 60 * 60 * 1000, pendingIntent);
    }

}