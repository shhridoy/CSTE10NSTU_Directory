package com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cste10nstu.shhridoy.cste10nstu.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItems> itemsList = null;
    private Context context;

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

        if (listItem.getImageUrl() != null) {
            Picasso.with(context)
                    .load(listItem.getImageUrl())
                    .into(holder.imageView);
        } else {
            String fileName = imageName(listItem.getId().trim());
            String path = "sdcard/cste10nstu/"+fileName;
            Picasso.with(context).load(new File(path)).into(holder.imageView);
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

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.nameTV);
            textViewid = itemView.findViewById(R.id.idTv);
            textViewMobile = itemView.findViewById(R.id.mobileTv);
            imageView = itemView.findViewById(R.id.imageTV);
            callButton = itemView.findViewById(R.id.callButton);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, textViewName.getText().toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context,"SMS to "+textViewName.getText().toString(),Toast.LENGTH_LONG).show();
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

    private String imageName(String stdId) {
        switch (stdId)
        {
            case "ID: ASH1501002M":
                return "image_cste0.jpg";
            case "ID: ASH1501003M":
                return "image_cste1.jpg";
            case "ID: ASH1501005M":
                return "image_cste2.jpg";
            case "ID: BKH1501006F":
                return "image_cste3.jpg";
            case "ID: ASH1501007M":
                return "image_cste4.jpg";
            case "ID: ASH1501008M":
                return "image_cste5.jpg";
            case "ID: BKH1501010F":
                return "image_cste6.jpg";
            case "ID: ASH1501011M":
                return "image_cste7.jpg";
            case "ID: ASH1501013M":
                return "image_cste8.jpg";
            case "ID: ASH1501014M":
                return "image_cste9.jpg";
            case "ID: BKH1501015F":
                return "image_cste10.jpg";
            case "ID: BKH1501016F":
                return "image_cste11.jpg";
            case "ID: ASH1501017M":
                return "image_cste12.jpg";
            case "ID: ASH1501018M":
                return "image_cste13.jpg";
            case "ID: ASH1501019M":
                return "image_cste14.jpg";
            case "ID: BKH1501021F":
                return "image_cste15.jpg";
            case "ID: ASH1501022M":
                return "image_cste16.jpg";
            case "ID: BKH1501023F":
                return "image_cste17.jpg";
            case "ID: ASH1501024M":
                return "image_cste18.jpg";
            case "ID: ASH1501025M":
                return "image_cste19.jpg";
            case "ID: ASH1501026M":
                return "image_cste20.jpg";
            case "ID: ASH1501027M":
                return "image_cste21.jpg";
            case "ID: BKH1501028F":
                return "image_cste22.jpg";
            case "ID: ASH1501029M":
                return "image_cste23.jpg";
            case "ID: ASH1501030M":
                return "image_cste24.jpg";
            case "ID: BKH1501031F":
                return "image_cste25.jpg";
            case "ID: ASH1501032M":
                return "image_cste26.jpg";
            case "ID: ASH1501033M":
                return "image_cste27.jpg";
            case "ID: ASH1501034M":
                return "image_cste28.jpg";
            case "ID: ASH1501035M":
                return "image_cste29.jpg";
            case "ID: BKH1501036F":
                return "image_cste30.jpg";
            case "ID: ASH1501037M":
                return "image_cste31.jpg";
            case "ID: ASH1501040M":
                return "image_cste32.jpg";
            case "ID: BKH1501041F":
                return "image_cste33.jpg";
            case "ID: BKH1501042F":
                return "image_cste34.jpg";
            case "ID: ASH1501045M":
                return "image_cste35.jpg";
            case "ID: BKH1501047F":
                return "image_cste36.jpg";
            case "ID: ASH1501048M":
                return "image_cste37.jpg";
            case "ID: ASH1501049M":
                return "image_cste38.jpg";
            case "ID: BKH1501050F":
                return "image_cste39.jpg";
            case "ID: ASH1501051M":
                return "image_cste40.jpg";
            case "ID: ASH1501052M":
                return "image_cste41.jpg";
            case "ID: ASH1501053M":
                return "image_cste42.jpg";
            case "ID: ASH1501054M":
                return "image_cste43.jpg";
            case "ID: ASH1501055M":
                return "image_cste44.jpg";
            case "ID: ASH1501056M":
                return "image_cste45.jpg";
            case "ID: BKH1501057F":
                return "image_cste46.jpg";
            case "ID: ASH1501058M":
                return "image_cste47.jpg";
            case "ID: ASH1501059M":
                return "image_cste48.jpg";
            case "ID: ASH1501060M":
                return "image_cste49.jpg";
            case "ID: BKH1501061F":
                return "image_cste50.jpg";
            case "ID: BKH1501062F":
                return "image_cste51.jpg";
            case "ID: ASH1501063M":
                return "image_cste52.jpg";
            case "ID: ASH1401015M":
                return "image_cste53.jpg";
            case "ID: ASH1401043M":
                return "image_cste54.jpg";
            default:
                return "image_cste37.jpg";
        }
    }

}