package com.cste10nstu.shhridoy.cste10nstu;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dream Land on 10/26/2017.
 */

public class BroadcastManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String yourDate = "26/10/2017";
            String yourHour = "01:20:00";
            Date d = new Date();
            @SuppressLint("SimpleDateFormat") DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat hour = new SimpleDateFormat("HH:mm:ss");
            //noinspection EqualsBetweenInconvertibleTypes
            if (date.equals(yourDate) && hour.equals(yourHour)) {
                Intent it = new Intent(context, MainActivity.class);
                createNotification(context, it, "New message", "Title", "This is a message body");
            }
        } catch (Exception e) {
            Log.i("date", "error == " + e.getMessage());
        }
    }

    public void createNotification(Context context, Intent intent, CharSequence ticker, CharSequence title, CharSequence descricao) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setContentText(descricao);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentIntent(p);
        Notification n = builder.build();
        //create the notification
        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        if (nm != null) {
            nm.notify(R.drawable.ic_launcher_background, n);
        }
        //create a vibration
        try {

            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        } catch (Exception e) {

        }
    }
}