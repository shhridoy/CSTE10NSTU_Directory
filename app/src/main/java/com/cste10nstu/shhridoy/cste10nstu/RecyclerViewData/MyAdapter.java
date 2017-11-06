package com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cste10nstu.shhridoy.cste10nstu.R;
import com.cste10nstu.shhridoy.cste10nstu.SecondActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItems> itemsList = null;
    private Context context;
    private int previousPosition = 0;

    public MyAdapter(List<ListItems> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListItems listItem = itemsList.get(position);


        holder.textViewName.setText(listItem.getName());
        holder.textViewid.setText(getSpanableBoldString(listItem.getId(), 3));
        holder.textViewMobile.setText(getSpanableBoldString(listItem.getMobile(), 8));

        if (position > previousPosition) { // scrolling down
            AnimationUtil.animate(holder, true);
        } else { // scrolling up
            AnimationUtil.animate(holder, false);
        }
        previousPosition = position;

        if (listItem.getImageUrl() != null) {
            Picasso.with(context).load(listItem.getImageUrl()).into(holder.imageView);
            holder.imageUrl = listItem.getImageUrl();
        } else {
            String fileName = listItem.getName()+".jpg";
            String path = "sdcard/cste10nstu/"+fileName;
            Picasso.with(context).load(new File(path)).into(holder.imageView);
            holder.imagePath = path;
        }


        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+Uri.encode(listItem.getMobile().substring(9).trim())));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

        TextView textViewName, textViewid, textViewMobile;
        ImageView imageView;
        ImageButton callButton;
        RelativeLayout rlItem;
        String imageUrl, imagePath;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.nameTV);
            textViewid = itemView.findViewById(R.id.idTv);
            textViewMobile = itemView.findViewById(R.id.mobileTv);
            imageView = itemView.findViewById(R.id.imageTV);
            callButton = itemView.findViewById(R.id.callButton);
            rlItem = itemView.findViewById(R.id.RLItem);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SecondActivity.class);
            intent.putExtra("Name", textViewName.getText().toString());
            intent.putExtra("ImageUrl", imageUrl);
            intent.putExtra("ImagePath", imagePath);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle(getSpanableString(textViewName.getText().toString()));
            MenuItem call = contextMenu.add(Menu.NONE, 1, 1, getSpanableString("Call"));
            MenuItem sms = contextMenu.add(Menu.NONE, 2, 2, getSpanableString("SMS"));
            //groupId, itemId, order, title
            call.setOnMenuItemClickListener(onChange);
            sms.setOnMenuItemClickListener(onChange);
        }

        private SpannableString getSpanableString (String s) {
            SpannableString ss = new SpannableString(s);
            ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, ss.length(), 0);
            return ss;
        }

        private final MenuItem.OnMenuItemClickListener onChange = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String mobileNo = textViewMobile.getText().toString().substring(9).trim();
                switch (item.getItemId()) {
                    case 1:
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+Uri.encode(mobileNo)));
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return true;
                        }
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(callIntent);
                        return true;

                    case 2:
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("smsto:"));
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address" , mobileNo);
                        smsIntent.putExtra("sms_body", "Type sms for "+textViewName.getText().toString());
                        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(smsIntent);
                        return true;
                }
                return false;
            }
        };
    }

    private SpannableStringBuilder getSpanableBoldString(String s, int endIndex) {
        SpannableStringBuilder str = new SpannableStringBuilder(s);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

}