package com.cste10nstu.shhridoy.cste10nstu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cste10nstu.shhridoy.cste10nstu.ListViewData.ContactCustomAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    ListView lvMobile, lvEmail;
    TextView tvName, tvId;
    ImageView imageView;
    ArrayList<String> arrayList1, arrayList2;
    ContactCustomAdapter contactCustomAdapter, contactCustomAdapter2;
    String name, id, mobile, imageUrl, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        name = i.getStringExtra("Name");
        id = i.getStringExtra("Id");
        mobile = i.getStringExtra("Mobile");
        imageUrl = i.getStringExtra("ImageUrl");
        imagePath = i.getStringExtra("ImagePath");

        initializeViews();

        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(imageView);
        } else if (imagePath != null) {
            Picasso.with(this).load(new File(imagePath)).into(imageView);
        }

        if (name != null) {
            tvName.setText(name);
        }

        if (id != null) {
            tvId.setText(id);
        }

        arrayList1 = new ArrayList<>();
        if (mobile != null) {
            arrayList1.add(mobile);
        } else {
            arrayList1.add("01742423628");
        }
        arrayList1.add("01878045480");
        contactCustomAdapter = new ContactCustomAdapter(this, arrayList1, 1);
        lvMobile.setAdapter(contactCustomAdapter);

        arrayList2 = new ArrayList<>();
        arrayList2.add("md.saifulhq@gmail.com");
        arrayList2.add("shhridoy.cste@gmail.com");
        contactCustomAdapter2 = new ContactCustomAdapter(this, arrayList2, 2);
        lvEmail.setAdapter(contactCustomAdapter2);

        ListUtils.setDynamicHeight(lvMobile);
        ListUtils.setDynamicHeight(lvEmail);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });
    }

    private void initializeViews () {
        imageView = findViewById(R.id.ImageViewSecond);
        tvName = findViewById(R.id.NameTextView);
        tvId = findViewById(R.id.IdTextView);
        lvMobile = findViewById(R.id.MobileListView);
        lvEmail = findViewById(R.id.EmailListView);
    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

}
