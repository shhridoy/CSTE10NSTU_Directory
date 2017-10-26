package com.cste10nstu.shhridoy.cste10nstu;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class SecondActivity extends AppCompatActivity {

    private NotificationCompat.Builder notification;
    private static final int UNIQUE_ID = 123242;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //notification = new NotificationCompat.Builder(this);
        //notification.setAutoCancel(true);

        boolean alarm = (PendingIntent.getBroadcast(this, 0, new Intent("ALARM_ACTION"), PendingIntent.FLAG_NO_CREATE) == null);

        if (alarm) {
            Intent alarmInent = new Intent("ALARM_ACTION");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, alarmInent,0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 3);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            BroadcastManager broadcastManager = new BroadcastManager();
            IntentFilter filter = new IntentFilter("ALARM_ACTION");
            registerReceiver(broadcastManager, filter);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60000, pendingIntent);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        /*AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Receiver receiver = new Receiver();
        IntentFilter filter = new IntentFilter("ALARM_ACTION");
        registerReceiver(receiver, filter);

        Intent intent = new Intent("ALARM_ACTION");
        intent.putExtra("param", "My scheduled action");
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
        // I choose 3s after the launch of my application
        alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000, operation) ;*/

       /* findViewById(R.id.notificationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification.setSmallIcon(R.drawable.ic_launcher_background);
                notification.setTicker("This is my Ticker");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle("This is my Title");
                notification.setContentText("This is the body of my text Notification");

                Intent intent = new Intent(SecondActivity.this, SecondActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(SecondActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);

                Notification n = notification.build();
                //create the notification
                n.vibrate = new long[]{150, 300, 150, 400};
                n.flags = Notification.FLAG_AUTO_CANCEL;

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.notify(UNIQUE_ID, n);
                }

                //create a vibration
                try {

                    Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone toque = RingtoneManager.getRingtone(getApplicationContext(), som);
                    toque.play();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }); */
    }

}
