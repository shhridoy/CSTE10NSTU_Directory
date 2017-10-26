package com.cste10nstu.shhridoy.cste10nstu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Dream Land on 10/26/2017.
 */

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context, intent.getStringExtra("param"),Toast.LENGTH_SHORT).show();
    }

}