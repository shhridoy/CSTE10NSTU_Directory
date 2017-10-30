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

import com.cste10nstu.shhridoy.cste10nstu.ListViewData.ContactCustomAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvMobile, lvEmail, lvSocial;
    TextView tvName, tvId, tvBirtDate, tvHomeTown;
    ImageView imageView;
    ArrayList<String> arrayListMobile, arrayListEmail, arrayListFB;
    ContactCustomAdapter contactCustomAdapter, contactCustomAdapter2, contactCustomAdapter3;
    String name, id, mobile, imageUrl, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        initializeViews();
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        name = i.getStringExtra("Name");
        id = i.getStringExtra("Id");
        mobile = i.getStringExtra("Mobile");
        imageUrl = i.getStringExtra("ImageUrl");
        imagePath = i.getStringExtra("ImagePath");

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

        arrayListMobile = new ArrayList<>();
        if (mobile != null) {
            arrayListMobile.add(mobile);
        } else {
            arrayListMobile.add("01742423628");
        }
        arrayListMobile.add("01878045480");
        contactCustomAdapter = new ContactCustomAdapter(this, arrayListMobile, 1);
        lvMobile.setAdapter(contactCustomAdapter);

        arrayListEmail = new ArrayList<>();
        arrayListEmail.add("md.saifulhq@gmail.com");
        arrayListEmail.add("shhridoy.cste@gmail.com");
        contactCustomAdapter2 = new ContactCustomAdapter(this, arrayListEmail, 2);
        lvEmail.setAdapter(contactCustomAdapter2);

        arrayListFB = new ArrayList<>();
        arrayListFB.add("https://www.facebook.com/unwantedhridoy.cptrii");
        contactCustomAdapter3 = new ContactCustomAdapter(this, arrayListFB, 3);
        lvSocial.setAdapter(contactCustomAdapter3);

        ListUtils.setDynamicHeight(lvMobile);
        ListUtils.setDynamicHeight(lvEmail);
        ListUtils.setDynamicHeight(lvSocial);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                fab.hide();
            }
        });
    }

    private void initializeViews () {
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.ImageViewSecond);
        tvName = findViewById(R.id.NameTextView);
        tvId = findViewById(R.id.IdTextView);
        lvMobile = findViewById(R.id.MobileListView);
        lvEmail = findViewById(R.id.EmailListView);
        lvSocial = findViewById(R.id.SocialListView);
        tvBirtDate = findViewById(R.id.TVBirthDate);
        tvHomeTown = findViewById(R.id.TVHomeTown);
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
