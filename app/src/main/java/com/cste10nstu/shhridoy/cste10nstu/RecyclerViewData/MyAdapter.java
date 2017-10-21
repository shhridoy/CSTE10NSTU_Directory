package com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData;

import android.Manifest;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.DBHelperImage;
import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.Utils;
import com.cste10nstu.shhridoy.cste10nstu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListItems listItem = itemsList.get(position);

        holder.textViewName.setText(listItem.getName());
        holder.textViewid.setText(listItem.getId());
        holder.textViewMobile.setText(listItem.getMobile());

        /*
        String imageUrl;
        if (listItem.getImageUrl().length() > 25) {
            imageUrl = listItem.getImageUrl();
        } else {
            imageUrl = "https://graph.facebook.com/" + listItem.getImageUrl() + "/picture?type=normal";
        }*/

        if (listItem.getImageUrl() != null) {
            Picasso.with(context)
                    .load(listItem.getImageUrl())
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageDrawable(Drawable.createFromPath("sdcard/cste10nstu/image_cste"+position+".jpg"));
        }


        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked: " + listItem.getName(), Toast.LENGTH_LONG).show();
            }
        });

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


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName, textViewid, textViewMobile;
        ImageView imageView;
        ImageButton callButton;
        RelativeLayout rl;

        ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.nameTV);
            textViewid = (TextView) itemView.findViewById(R.id.idTv);
            textViewMobile = itemView.findViewById(R.id.mobileTv);
            imageView = (ImageView) itemView.findViewById(R.id.imageTV);
            callButton = itemView.findViewById(R.id.callButton);
            rl = (RelativeLayout) itemView.findViewById(R.id.RLItem);

        }
    }

}