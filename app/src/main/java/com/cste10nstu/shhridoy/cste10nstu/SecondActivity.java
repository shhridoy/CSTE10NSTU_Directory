package com.cste10nstu.shhridoy.cste10nstu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cste10nstu.shhridoy.cste10nstu.ListViewData.CustomAdapter;
import com.cste10nstu.shhridoy.cste10nstu.ListViewData.ListUtils;
import com.cste10nstu.shhridoy.cste10nstu.MyAnimations.AnimationUtil;
import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.DBHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SecondActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvMobile, lvEmail, lvSocial;
    TextView tvName, tvId, tvBirtDate, tvHomeTown, tv1, tv2, tv3, tv4;
    ImageView imageView;
    LinearLayout ll1, ll2, ll3;
    ArrayList<String> arrayListMobile, arrayListEmail, arrayListSocial;
    CustomAdapter customAdapter, customAdapter2, customAdapter3;
    String name, imageUrl, imagePath;

    String Id, Mobile_1, Mobile_2, DateOfBirth, Email_1, Email_2, Facebook_Url, Other_Url, Home_city;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        imageUrl = i.getStringExtra("ImageUrl");
        imagePath = i.getStringExtra("ImagePath");

        assignDataFromDatabase(Id);

        initializeViews();
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);

        animationFunction();

        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(imageView);
        } else if (imagePath != null) {
            Picasso.with(this).load(new File(imagePath)).into(imageView);
        } else {
            Picasso.with(this).load(new File("sdcard/cste10nstu/"+Id+".jpg")).into(imageView);
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

        arrayListSocial = new ArrayList<>();
        if (Facebook_Url != null) {
            arrayListSocial.add(Facebook_Url);
        }
        if (Other_Url != null && !Other_Url.equals(" ")) {
            String[] othersAdd = Other_Url.split(" ");
            arrayListSocial.addAll(Arrays.asList(othersAdd));
        }
        customAdapter3 = new CustomAdapter(this, arrayListSocial, 3);
        lvSocial.setAdapter(customAdapter3);

        if (DateOfBirth != null) {
            String[] splited = DateOfBirth.split("/");
            tvBirtDate.setText(stringFormatOfDate(splited[0], splited[1]));
        }

        if (Home_city != null) {
            tvHomeTown.setText(Home_city);
        }

        ListUtils.setDynamicHeight(lvMobile);
        ListUtils.setDynamicHeight(lvEmail);
        ListUtils.setDynamicHeight(lvSocial);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void assignDataFromDatabase(String std_id) {
        dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.retrieveData();
        while (cursor.moveToNext()) {
            if (cursor.getString(2).equals(std_id)){
                name = cursor.getString(1);
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
        ll1 = findViewById(R.id.LL1);
        ll2 = findViewById(R.id.BirthDateLL);
        ll3 = findViewById(R.id.HomeTownLL);
        tv1 = findViewById(R.id.TV1);
        tv2 = findViewById(R.id.TV2);
        tv3 = findViewById(R.id.TV3);
        tv4 = findViewById(R.id.TV4);
    }

    private void animationFunction () {
        AnimationUtil.rightToLeftAnimation(ll1, 600);

        AnimationUtil.leftToRightAnimation(tv1, 900);
        AnimationUtil.leftToRightAnimation(lvMobile, 900);

        AnimationUtil.rightToLeftAnimation(tv2, 1200);
        AnimationUtil.rightToLeftAnimation(lvEmail, 1200);

        AnimationUtil.leftToRightAnimation(tv3, 1500);
        AnimationUtil.leftToRightAnimation(lvSocial, 1500);

        AnimationUtil.rightToLeftAnimation(tv4, 1800);
        AnimationUtil.rightToLeftAnimation(ll2, 1800);
        AnimationUtil.rightToLeftAnimation(ll3, 1800);
    }

    private String stringFormatOfDate (String day, String month) {
        int d = Integer.parseInt(day);
        int m = Integer.parseInt(month);
        String firstPart, secondPart;

        if (d == 1) {
            firstPart = d+"st";
        } else if (d == 2) {
            firstPart = d+"nd";
        } else if (d == 3) {
            firstPart = d+"rd";
        } else {
            firstPart = d+"th";
        }

        switch (m) {
            case 1:
                secondPart = "January";
                break;
            case 2:
                secondPart = "February";
                break;
            case 3:
                secondPart = "March";
                break;
            case 4:
                secondPart = "April";
                break;
            case 5:
                secondPart = "May";
                break;
            case 6:
                secondPart = "June";
                break;
            case 7:
                secondPart = "July";
                break;
            case 8:
                secondPart = "August";
                break;
            case 9:
                secondPart = "September";
                break;
            case 10:
                secondPart = "October";
                break;
            case 11:
                secondPart = "November";
                break;
            case 12:
                secondPart = "December";
                break;
            default:
                secondPart = "December";
        }

        return firstPart+", "+secondPart;
    }

}
