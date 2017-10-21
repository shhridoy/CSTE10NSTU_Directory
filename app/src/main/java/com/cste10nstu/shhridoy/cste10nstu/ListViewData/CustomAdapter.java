package com.cste10nstu.shhridoy.cste10nstu.ListViewData;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.cste10nstu.shhridoy.cste10nstu.R;
import com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData.ListItems;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ListItems> arrayList;
    private LayoutInflater inflater;
    private ListItems listItems;

    public CustomAdapter(Context c, ArrayList<ListItems> arrayList) {
        this.context = c;
        this.arrayList = arrayList;
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
    public View getView(final int position, View view, final ViewGroup viewGroup) {

        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            if (inflater != null) {
                view = inflater.inflate(R.layout.list_item, viewGroup, false);
            }
        }

        //BIND DATA
        final MyViewHolder holder = new MyViewHolder(view);
        holder.nameTV.setText(arrayList.get(position).getName());
        holder.idTV.setText(arrayList.get(position).getId());
        holder.mobileTV.setText(arrayList.get(position).getMobile());

        if (arrayList.get(position).getImageUrl() != null) {
            Picasso.with(context)
                    .load(arrayList.get(position).getImageUrl())
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageDrawable(Drawable.createFromPath("sdcard/cste10nstu/image_cste"+position+".jpg"));
        }

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, arrayList.get(position).getName(), Toast.LENGTH_LONG).show();
            }
        });

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+Uri.encode(arrayList.get(position).getMobile().substring(9).trim())));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
            }
        });

        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MenuThirdDetails();
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("BOOK_ITEM", arrayList.get(position).getName());
                bundle.putString("TAG2", "Bookmarks");
                bundle.putInt("ID", arrayList.get(position).getId());
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });*/

        holder.setLongClickListener(new MyLongClickListener() {
            @Override
            public void onItemLongClick() {
                listItems = (ListItems) getItem(position);
            }
        });

        return view;
    }

    /*//EXPOSE NAME AND ID
    public int getSelectedItemId(){
        return listItems.getId();
    }*/

    public String getSelectedItemName(){
        return listItems.getName();
    }



}