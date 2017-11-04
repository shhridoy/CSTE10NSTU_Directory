package com.cste10nstu.shhridoy.cste10nstu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cste10nstu.shhridoy.cste10nstu.ListViewData.CustomAdapter;
import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.DBHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvMobile, lvEmail, lvSocial;
    TextView tvName, tvId, tvBirtDate, tvHomeTown;
    ImageView imageView;
    ArrayList<String> arrayListMobile, arrayListEmail, arrayListFB;
    CustomAdapter customAdapter, customAdapter2, customAdapter3;
    String name, imageUrl, imagePath;

    String Id, Mobile_1, Mobile_2, DateOfBirth, Email_1, Email_2, Facebook_Url, Other_Url, Home_city;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        initializeViews();
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        name = i.getStringExtra("Name");
        imageUrl = i.getStringExtra("ImageUrl");
        imagePath = i.getStringExtra("ImagePath");

        fetchDataFromDB(name);

        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(imageView);
        } else if (imagePath != null) {
            Picasso.with(this).load(new File(imagePath)).into(imageView);
        } else {
            Picasso.with(this).load(new File("sdcard/cste10nstu/"+name+".jpg")).into(imageView);
        }

        if (name != null) {
            tvName.setText(name);
        }

        if (Id != null) {
            tvId.setText(Id);
        }

        arrayListMobile = new ArrayList<>();
        if (Mobile_1 != null) {
            arrayListMobile.add(Mobile_1);
        }
        if (Mobile_2 != null && !Mobile_2.equals(" ")) {
            arrayListMobile.add(Mobile_2);
        }
        customAdapter = new CustomAdapter(this, arrayListMobile, 1);
        lvMobile.setAdapter(customAdapter);

        arrayListEmail = new ArrayList<>();
        if (Email_1 != null && !Email_1.equals(" ")) {
            arrayListEmail.add(Email_1);
        } else {
            arrayListEmail.add("none");
        }
        if (Email_2 != null && !Email_2.equals(" ")) {
            arrayListEmail.add(Email_2);
        }
        customAdapter2 = new CustomAdapter(this, arrayListEmail, 2);
        lvEmail.setAdapter(customAdapter2);

        arrayListFB = new ArrayList<>();
        if (Facebook_Url != null) {
            arrayListFB.add(Facebook_Url);
        }
        if (Other_Url != null && !Other_Url.equals(" ")) {
            arrayListFB.add(Other_Url);
        }
        customAdapter3 = new CustomAdapter(this, arrayListFB, 3);
        lvSocial.setAdapter(customAdapter3);

        if (DateOfBirth != null) {
            tvBirtDate.setText(DateOfBirth);
        }

        if (Home_city != null) {
            tvHomeTown.setText(Home_city);
        }

        ListUtils.setDynamicHeight(lvMobile);
        ListUtils.setDynamicHeight(lvEmail);
        ListUtils.setDynamicHeight(lvSocial);
    }

    private void fetchDataFromDB (String STD_NAME) {
        dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.retrieveData();
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(STD_NAME)){
                Id = cursor.getString(2);
                Mobile_1 = cursor.getString(3);
                DateOfBirth = cursor.getString(4);
                Mobile_2 = cursor.getString(6);
                Email_1 = cursor.getString(7);
                Email_2 = cursor.getString(8);
                Facebook_Url = cursor.getString(9);
                Other_Url = cursor.getString(10);
                Home_city = cursor.getString(11);
                break;
            }
        }
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
