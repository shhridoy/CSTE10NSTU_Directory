package com.cste10nstu.shhridoy.cste10nstu.ListViewData;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
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

import com.cste10nstu.shhridoy.cste10nstu.MyAnimations.AnimationUtil;
import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.DBHelper;
import com.cste10nstu.shhridoy.cste10nstu.R;
import com.cste10nstu.shhridoy.cste10nstu.SecondActivity;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> arrayList;
    private int tag;
    private ArrayList<BirthdayListItems> bthdArrayList;
    private String theme;

    public CustomAdapter(Context context, ArrayList<String> arrayList, int tag) {
        this.context = context;
        this.arrayList = arrayList;
        this.tag = tag;
        theme = PreferenceManager.getDefaultSharedPreferences(context).getString("Theme", "White");
    }

    public CustomAdapter(Context context, int tag, ArrayList<BirthdayListItems> bthdArrayList) {
        this.context = context;
        this.tag = tag;
        this.bthdArrayList = bthdArrayList;
        theme = PreferenceManager.getDefaultSharedPreferences(context).getString("Theme", "White");
    }

    @Override
    public int getCount() {
        return tag == 4 ? bthdArrayList.size() : arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return tag == 4 ? bthdArrayList.get(i) : arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            if (tag == 4) {
                view = inflater.inflate(R.layout.birthday_list_item, viewGroup, false);
            }else {
                view = inflater.inflate(R.layout.contact_list_item, viewGroup, false);
            }
        }

        if (tag == 4) {
            CardView cardView = view.findViewById(R.id.CardViewBirthdayListItem);
            TextView tvName = view.findViewById(R.id.NameTextViewBirthday);
            TextView tvDate = view.findViewById(R.id.BirthdateTextView);
            final BirthdayListItems birthdayListItems = bthdArrayList.get(position);
            tvName.setText(birthdayListItems.getName());
            tvName.setSelected(true);
            tvDate.setText(birthdayListItems.getDateOfBirth());
            if (theme.equals("Dark")) {
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.dark_color_secondary));
                tvName.setTextColor(Color.WHITE);
                tvDate.setTextColor(Color.WHITE);
            }
            //AnimationUtil.bottomToUpAnimation(view, 600+100*position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!birthdayListItems.getName().contains("No birthday!!")) {
                        Intent intent = new Intent(context, SecondActivity.class);
                        intent.putExtra("Id", getId(birthdayListItems.getName()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });

        } else {
            final ImageButton imageButton = view.findViewById(R.id.ImageButton);
            TextView textView = view.findViewById(R.id.TVCustom);
            final RelativeLayout relativeLayout = view.findViewById(R.id.RLContactListItem);
            final ImageView imageView = view.findViewById(R.id.ImageView);

            if (tag == 2) {
                imageView.setImageResource(R.drawable.ic_action_mail);
                if (theme.equals("Dark")) {
                    imageButton.setImageResource(R.drawable.ic_action_copy_white);
                } else {
                    imageButton.setImageResource(R.drawable.ic_action_cpoy);
                }
            } else if (tag == 3) {
                if (position == 0) {
                    imageView.setImageResource(R.drawable.facebook_icon2);
                } else {
                    if (arrayList.get(position).contains("instagram")) {
                        imageView.setImageResource(R.drawable.instagram_icon);
                    } else if (arrayList.get(position).contains("github")) {
                        if (theme.equals("Dark")) {
                            imageView.setImageResource(R.drawable.github_icon_white);
                        } else {
                            imageView.setImageResource(R.drawable.github_icon);
                        }
                    } else if (arrayList.get(position).contains("twitter")) {
                        imageView.setImageResource(R.drawable.twitter_icon);
                    } else if (arrayList.get(position).contains("linkedin")) {
                        imageView.setImageResource(R.drawable.linkedin_icon);
                    } else {
                        if (theme.equals("Dark")) {
                            imageView.setImageResource(R.drawable.ic_action_others_white);
                        } else {
                            imageView.setImageResource(R.drawable.ic_action_others);
                        }
                    }

                }
                if (theme.equals("Dark")) {
                    imageButton.setImageResource(R.drawable.ic_action_copy_white);
                } else {
                    imageButton.setImageResource(R.drawable.ic_action_cpoy);
                }
            }

            textView.setText(arrayList.get(position));
            textView.setSelected(true); // for working marque effect to text

            if (theme.equals("Dark")) {
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.dark_color_secondary));
                imageButton.setBackgroundColor(context.getResources().getColor(R.color.dark_color_secondary));
                textView.setTextColor(Color.WHITE);
                View view1 = view.findViewById(R.id.View);
                view1.setBackgroundColor(Color.WHITE);
            }

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tag == 1) {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("smsto:"));
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address" , arrayList.get(position).trim());
                        smsIntent.putExtra("sms_body", "Type sms for "+getName(arrayList.get(position)));
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(smsIntent);
                    } else {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Email", arrayList.get(position).trim());
                        if (clipboard != null) {
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, arrayList.get(position)+" copied to clipboard!", Toast.LENGTH_LONG).show();
                        }
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
                    if (tag == 1) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+Uri.encode(arrayList.get(position).trim())));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(callIntent);
                    } else if (tag == 2) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",arrayList.get(position).trim(), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));

                    } else {
                        Uri uri = Uri.parse(arrayList.get(position).trim());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                }
            });

            final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Contact", arrayList.get(position).trim());
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, arrayList.get(position)+" copied to clipboard!", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        case 2:
                            String chooserTag;
                            if (tag == 1) {
                                chooserTag = "Share mobile number via";
                            } else if (tag == 2) {
                                chooserTag = "Share email address via";
                            } else {
                                chooserTag = "Share the link via";
                            }
                            String textShare = getName(arrayList.get(position))+"\n"+arrayList.get(position);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, textShare);
                            shareIntent.setType("text/plain");
                            context.startActivity(Intent.createChooser(shareIntent, chooserTag));
                            return true;
                    }
                    return false;
                }
            };

            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    String contextMenuItemTitle;
                    if (tag == 1) {
                        contextMenuItemTitle = "Share mobile number";
                    } else if (tag == 2) {
                        contextMenuItemTitle = "Share email address";
                    } else {
                        contextMenuItemTitle = "Share the link";
                    }

                    contextMenu.setHeaderTitle(getSpanableString(arrayList.get(position)));
                    MenuItem copy = contextMenu.add(Menu.NONE, 1, 1, getSpanableString("Copy to clipboard"));
                    MenuItem share = contextMenu.add(Menu.NONE, 2, 2, getSpanableString(contextMenuItemTitle));
                    //groupId, itemId, order, title
                    copy.setOnMenuItemClickListener(onChange);
                    share.setOnMenuItemClickListener(onChange);
                }
            });
        }

        notifyDataSetChanged();
        return view;
    }

    private SpannableString getSpanableString (String s) {
        SpannableString ss = new SpannableString(s);
        ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, ss.length(), 0);
        return ss;
    }

    private String getName (String mobile_no) {
        DBHelper databaseHelper = new DBHelper(context);
        Cursor c = databaseHelper.retrieveData();
        String Name = "Saiful Haque";
        while (c.moveToNext()){
            String name = c.getString(1);
            String st_mobile = c.getString(3);
            String st_mobile2 = c.getString(6);
            if (mobile_no.equals(st_mobile) || mobile_no.equals(st_mobile2)) {
                Name = name;
                break;
            }
        }
        return Name;
    }

    private String getId(String name) {
        DBHelper dbHelper = new DBHelper(context);
        Cursor c = dbHelper.retrieveData();
        String id = "ASH1501037M";
        while (c.moveToNext()) {
            if (c.getString(1).equals(name)){
                id = c.getString(2);
                break;
            }
        }
        return id;
    }
}
