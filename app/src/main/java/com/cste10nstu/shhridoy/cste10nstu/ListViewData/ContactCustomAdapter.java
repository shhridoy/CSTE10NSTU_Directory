package com.cste10nstu.shhridoy.cste10nstu.ListViewData;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cste10nstu.shhridoy.cste10nstu.R;

import java.util.ArrayList;

public class ContactCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> arrayList;
    private int tag;

    public ContactCustomAdapter(Context context, ArrayList<String> arrayList, int tag) {
        this.context = context;
        this.arrayList = arrayList;
        this.tag = tag;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            view = inflater.inflate(R.layout.contact_list_item, viewGroup, false);
        }
        final ImageButton imageButton = view.findViewById(R.id.ImageButton);
        TextView textView = view.findViewById(R.id.TVCustom);
        final RelativeLayout relativeLayout = view.findViewById(R.id.RL);
        final ImageView imageView = view.findViewById(R.id.ImageView);

        if (tag == 2) {
            imageView.setImageResource(R.drawable.ic_action_mail);
            imageButton.setImageResource(R.drawable.ic_action_copy);
        }

        textView.setText(arrayList.get(position));

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag == 2) {
                    Toast.makeText(context, arrayList.get(position)+" copied to clipboard! (TEST)", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Sending message to "+arrayList.get(position)+" (TEST)", Toast.LENGTH_LONG).show();
                }
                imageButton.setBackgroundColor(Color.LTGRAY);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageButton.setBackgroundColor(Color.WHITE);
                    }
                }, 250);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setBackgroundColor(Color.LTGRAY);
                imageView.setBackgroundColor(Color.LTGRAY);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout.setBackgroundColor(Color.WHITE);
                        imageView.setBackgroundColor(Color.WHITE);
                    }
                }, 250);

                if (tag == 2) {
                    Toast.makeText(context, "Sending email to "+arrayList.get(position)+" (TEST)", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Calling to "+arrayList.get(position)+" (TEST)", Toast.LENGTH_LONG).show();
                }
            }
        });

        final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String mobileNo = arrayList.get(position);
                switch (item.getItemId()) {
                    case 1:
                        /*Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+Uri.encode(mobileNo)));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return true;
                        }
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(callIntent); */
                        Toast.makeText(context, "Content copy to clipboard. (TEST)", Toast.LENGTH_LONG).show();
                        return true;
                    case 2:
                        /*Toast.makeText(context,"SMS to "+arrayList.get(position),Toast.LENGTH_LONG).show();
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("smsto:"));
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address" , mobileNo);
                        smsIntent.putExtra("sms_body", "Type sms for "+arrayList.get(position));
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(smsIntent);*/
                        Toast.makeText(context, "Sharing intent (TEST)", Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        };

        view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

                relativeLayout.setBackgroundColor(Color.LTGRAY);
                imageView.setBackgroundColor(Color.LTGRAY);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout.setBackgroundColor(Color.WHITE);
                        imageView.setBackgroundColor(Color.WHITE);
                    }
                }, 250);

                contextMenu.setHeaderTitle(getSpanableString(arrayList.get(position)));
                MenuItem copy = contextMenu.add(Menu.NONE, 1, 1, getSpanableString("Copy to clipboard."));
                MenuItem share = contextMenu.add(Menu.NONE, 2, 2, getSpanableString("Share the contact."));
                //groupId, itemId, order, title
                copy.setOnMenuItemClickListener(onChange);
                share.setOnMenuItemClickListener(onChange);
            }
        });

        notifyDataSetChanged();
        return view;
    }

    private SpannableString getSpanableString (String s) {
        SpannableString ss = new SpannableString(s);
        ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, ss.length(), 0);
        return ss;
    }
}
