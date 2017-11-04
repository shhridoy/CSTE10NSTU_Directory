package com.cste10nstu.shhridoy.cste10nstu;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.DBHelper;
import com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData.ListItems;
import com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData.MyAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class NotificationReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        int currMonth = c.get(Calendar.MONTH)+1; // count month from 0 to 11
        int currDay = c.get(Calendar.DATE);

        DBHelper databaseHelper = new DBHelper(context);
        Cursor cursor = databaseHelper.retrieveData();
        while (cursor.moveToNext()){
            //String st_id = cursor.getString(2);
            //String st_mobile = cursor.getString(3);
            String name = cursor.getString(1);
            String date = cursor.getString(4);
            String[] splitedDate = date.split("/");
            if (Integer.parseInt(splitedDate[0]) == currDay && Integer.parseInt(splitedDate[1]) == currMonth) {
                createNotification(context, intent, name);
                break;
            }
        }

    }

    public void createNotification (Context context, Intent intent, String name) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificIntent = new Intent(context, SecondActivity.class);
        notificIntent.putExtra("Name", name);
        notificIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, notificIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_birtday_icon)
                .setTicker("Birthday")
                .setContentTitle(name+"'s Birthday")
                .setContentText("Today "+name+"'s birthday. Tap to view")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        if (notificationManager != null) {
            notificationManager.notify(100, builder.build());
        }
    }

}