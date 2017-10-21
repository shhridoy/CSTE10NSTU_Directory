package com.cste10nstu.shhridoy.cste10nstu.ListViewData;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cste10nstu.shhridoy.cste10nstu.R;

/**
 * Created by Dream Land on 10/21/2017.
 */

public class MyViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener{

    TextView nameTV, idTV, mobileTV;
    ImageView imageView;
    RelativeLayout rl;
    ImageButton callButton;

    private MyLongClickListener longClickListener;

    public MyViewHolder(View v) {
        iniNameText(v);
        v.setOnLongClickListener(this);
        v.setOnCreateContextMenuListener(this);
    }

    private void iniNameText(View v){
        nameTV = v.findViewById(R.id.nameTV);
        idTV = v.findViewById(R.id.idTv);
        mobileTV = v.findViewById(R.id.mobileTv);
        imageView = v.findViewById(R.id.imageTV);
        callButton = v.findViewById(R.id.callButton);
        rl = (RelativeLayout) v.findViewById(R.id.RLItem);
    }

    @Override
    public boolean onLongClick(View view) {
        this.longClickListener.onItemLongClick();
        return false;
    }

    public void setLongClickListener(MyLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
        String value = nameTV.getText().toString();

        contextMenu.setHeaderTitle((CharSequence) value);
        contextMenu.add(0, 0, 0, "Call");
        contextMenu.add(0, 1, 0, "Message");
    }
}
