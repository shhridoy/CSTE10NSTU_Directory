package com.cste10nstu.shhridoy.cste10nstu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cste10nstu.shhridoy.cste10nstu.ListViewData.BirthdayListItems;
import com.cste10nstu.shhridoy.cste10nstu.ListViewData.CustomAdapter;
import com.cste10nstu.shhridoy.cste10nstu.ListViewData.ListUtils;
import com.cste10nstu.shhridoy.cste10nstu.MyAnimations.AnimationUtil;
import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.Constants;
import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.DBHelper;
import com.cste10nstu.shhridoy.cste10nstu.MyValues.Values;
import com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData.ListItems;
import com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData.MyAdapter;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String URL_LINK; // URL ADDRESS OF JSON DATA
    private static List<String> URL_List; // contains all downloadable image URL
    private static int num = 0; // controls the sync task operation while downloading images
    private static int LENGTH; // contains the length of the json array or number of rows in database

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> itemsList;
    private String toast;
    private List<String> mobileNoList; // contains all mobile no. for messaging
    FloatingActionButton fab; // SYNC FLOATING ACTION BUTTON

    private DBHelper dbHelper;
    private Cursor cursor;
    private Boolean noData; // for checking if there are any data exists in the database
    private boolean dataSynced, inFavorite, inBDlists;
    private static int trn = 0;

    private Toolbar toolbar;
    private RelativeLayout rlMain;
    private LinearLayout llContact, llBirthdays, llFavorite;
    private TextView contactTv, favoriteTv, birthdayTv, footerTv;
    private ScrollView scrollView;
    private String theme; // string value for app modes

    // NOT NEEDED
    private static final int CALL_PERMISSION_CODE = 55;
    private static final int WRITE_SD_PERMISSION_CODE = 77;
    private static final int SMS_PERMISSION_CODE = 87;

    // PERMISSION CODE
    public static final int MULTIPLE_PERMISSIONS = 10;

    // PERMISSION LIST
    String[] permissionsList = new String[] {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.INTERNET,
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SET_ALARM,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL_LINK = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("MY_URL", Values.FIRST_URL);

        theme = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("Theme", "White");

        initializeViews();
        changingTheme();
        setSupportActionBar(toolbar);

        scrollView.setVisibility(View.INVISIBLE);
        footerTv.setVisibility(View.INVISIBLE);

        URL_List = new ArrayList<>();
        itemsList = new ArrayList<>();
        mobileNoList = new ArrayList<>();

        dbHelper = new DBHelper(this);
        noData = dbHelper.retrieveData().getCount() == 0;
        dataSynced = false;
        inFavorite = false;
        inBDlists = false;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (inFavorite || inBDlists) {
                    fab.hide();
                } else {
                    fab.show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fab.hide();
                    }
                }, 6000);
                return false;
            }
        });

        if (isInternetOn()) {
            fab.hide();
            loadRecyclerViewFromJson();
        } else {
            if(noData) {
                fab.show();
                Snackbar.make(findViewById(R.id.coordinatorMain), "Please turn your internet connection on to sync the data first time!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                loadRecyclerViewFromDatabase();
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetOn()) {
                    loadRecyclerViewFromJson();
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorMain), "Please check you internet connection!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        selectionFunction();

        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            checkPermissions();
        }

        setNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    String query = newText.toLowerCase();

                    final List<ListItems> filteredList = new ArrayList<>();

                    for (int i = 0; i < itemsList.size(); i++) {

                        final String text = itemsList.get(i).getName().toLowerCase();
                        if (text.contains(query)) {
                            filteredList.add(itemsList.get(i));
                        }
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    adapter = new MyAdapter(filteredList, MainActivity.this, 1);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();  // data set changed
                    return false;
                }
            });

            return true;

        } else if (id == R.id.menu_item_downloadImage) {
            AlertDialog.Builder builder;
            //writeInSDCardPermission();
            if (theme.equals("Dark")) {
                builder = new AlertDialog.Builder(this, R.style.MyDialogThemeDark);
                builder.setMessage(Html.fromHtml("<font color='#ffffff'>Do you want to download images for offline?</font>"));
                builder.setTitle(Html.fromHtml("<font color='#FFFF00'>Warning!!</font>"));
            } else {
                builder = new AlertDialog.Builder(this);
                builder.setMessage(Html.fromHtml("<font color='#000000'>Do you want to download images for offline?</font>"));
                builder.setTitle(Html.fromHtml("<font color='#1DC4ED'>Warning!!</font>"));
            }
            builder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (isInternetOn()) {
                        if (num >= LENGTH) {
                            num = 0;
                        }
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(URL_List.get(num));
                    } else {
                        Snackbar.make(findViewById(R.id.coordinatorMain), "Please check you internet connection!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            return true;

        } else if (id == R.id.menu_item_smstoall) {
            //smsPermission();
            typeSMSDialog();
            return true;
        } else if (id == R.id.menu_item_inpur_url) {
            urlInputDialog();
            return true;
        } else if (id == R.id.menu_item_themes) {
            themesDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    boolean backButtonPressedOnce = false; // to check how many click occurs in back button
    @Override
    public void onBackPressed() {
        if(backButtonPressedOnce){
            super.onBackPressed();
            return;
        }

        this.backButtonPressedOnce = true;
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorMain), "Please Press Back Again to Exit", Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backButtonPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    String permissionss = "";
                    for (String per : permissionsList) {
                        permissionss += "\n" + per;
                    }
                    // permissions list of don't granted permission
                    Toast.makeText(this, "Permission doesn't granted.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case CALL_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access of making call is granted.", Toast.LENGTH_SHORT).show();
                } else {
                    //code for deny
                    Toast.makeText(this, "Access of making call is denied.", Toast.LENGTH_SHORT).show();
                }
                break;

            case WRITE_SD_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access of write in sdcard is granted.", Toast.LENGTH_SHORT).show();
                } else {
                    //code for deny
                    Toast.makeText(this, "Access of write in sdcard is denied.", Toast.LENGTH_SHORT).show();
                }
                break;

            case SMS_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access of sending SMS is granted.", Toast.LENGTH_SHORT).show();
                } else {
                    //code for deny
                    Toast.makeText(this, "Access of sending SMS is denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        rlMain = findViewById(R.id.RLContentMain);
        llContact = findViewById(R.id.LLContact);
        llBirthdays = findViewById(R.id.LLBirthdays);
        llFavorite = findViewById(R.id.LLFavorite);
        scrollView = findViewById(R.id.scrollView);
        recyclerView = findViewById(R.id.RecyclerView);
        fab = findViewById(R.id.fab);
        contactTv = findViewById(R.id.ContactTV);
        favoriteTv = findViewById(R.id.FavoriteTV);
        birthdayTv = findViewById(R.id.BirthdayTv);
        footerTv = findViewById(R.id.footerTV);
    }

    private void loadRecyclerViewFromJson() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data....");
        progressDialog.setCancelable(false);
        if (!dataSynced) {
            progressDialog.show();
        } else {
            progressDialog.hide();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_LINK,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        dataSynced = true;

                        try {
                            itemsList.clear();
                            URL_List.clear();
                            mobileNoList.clear();
                            LENGTH = 0;

                            JSONArray jsonArray = new JSONArray(response);
                            LENGTH = jsonArray.length();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = (JSONObject) jsonArray.get(i);

                                String imageUrl = object.getString("imageId").equals(" ") ?
                                        object.getString("extraImage") :
                                        "https://graph.facebook.com/" + object.getString("imageId") + "/picture?type=normal";

                                String imageForDownload = object.getString("downloadableImage");
                                URL_List.add(imageForDownload);

                                ListItems list = new ListItems(
                                        object.getString("name"),
                                        "ID: " + object.getString("id"),
                                        "CONTACT: " + object.getString("mobile"),
                                        imageUrl
                                );

                                mobileNoList.add(object.getString("mobile"));

                                if (noData) {
                                    saveData(
                                            object.getString("name"),
                                            object.getString("id"),
                                            object.getString("mobile"),
                                            object.getString("dateOfBirth"),
                                            object.getString("downloadableImage"),
                                            object.getString("mobile_2"),
                                            object.getString("email_1"),
                                            object.getString("email_2"),
                                            object.getString("facebook_link"),
                                            object.getString("others"),
                                            object.getString("home_city")
                                    );
                                } else {
                                    updateData(
                                            object.getString("name"),
                                            object.getString("id"),
                                            object.getString("mobile"),
                                            object.getString("dateOfBirth"),
                                            object.getString("downloadableImage"),
                                            object.getString("mobile_2"),
                                            object.getString("email_1"),
                                            object.getString("email_2"),
                                            object.getString("facebook_link"),
                                            object.getString("others"),
                                            object.getString("home_city")
                                    );
                                }

                                itemsList.add(list);
                                adapter = new MyAdapter(itemsList, getApplicationContext(), 1);
                                recyclerView.setAdapter(adapter);
                            }

                            if (toast != null) {
                                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "First URL Failed!!", Toast.LENGTH_SHORT).show();
                        // SECOND URL LINK WILL WORK IF FIRST ONE IS FAILED
                        URL_LINK = Values.SECOND_URL;
                        trn++;
                        if (trn == 1) {
                            loadRecyclerViewFromJson();
                        } else {
                            loadRecyclerViewFromDatabase();
                        }
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadRecyclerViewFromDatabase(){
        itemsList.clear();
        URL_List.clear();
        mobileNoList.clear();
        LENGTH = 0;

        dbHelper = new DBHelper(this);
        cursor = dbHelper.retrieveData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "Data doesn't sync yet!!",Toast.LENGTH_LONG).show();
        } else {
            int i = 0;
            while (cursor.moveToNext()){
                // Index starts from 1
                String name = cursor.getString(1);
                String st_id = cursor.getString(2);
                String st_mobile = cursor.getString(3);
                mobileNoList.add(st_mobile);

                String downloadableImageUrl = cursor.getString(5);
                URL_List.add(downloadableImageUrl);

                ListItems listItems = new ListItems(name, "ID: " +st_id, "CONTACT: "+st_mobile);
                itemsList.add(listItems);
                adapter = new MyAdapter(itemsList, getApplicationContext(), 1);
                recyclerView.setAdapter(adapter);
                i++;
            }
            LENGTH = i;
        }
    }

    public void loadFavoriteRecyclerView() {
        itemsList.clear();
        dbHelper = new DBHelper(this);
        Cursor cur = dbHelper.retrieveFavData();
        if (cur.getCount() == 0) {
            ListItems listItems = new ListItems("No Contact", "ID: NULL", "CONTACT: NULL");
            List<ListItems> dl = new ArrayList<>();
            dl.add(listItems);
            adapter = new MyAdapter(dl, getApplicationContext(), 2);
            recyclerView.setAdapter(adapter);
            Toast.makeText(this, "Favorite is empty", Toast.LENGTH_LONG).show();
        } else {
            while (cur.moveToNext()) {
                String name = cur.getString(1);
                String st_id = cur.getString(2);
                String st_mobile = cur.getString(3);
                ListItems listItems = new ListItems(name, "ID: " +st_id, "CONTACT: "+st_mobile);
                itemsList.add(listItems);
                adapter = new MyAdapter(itemsList, getApplicationContext(), 2);
                recyclerView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void saveData (
            String studentName, String studentId, String studentMblNo,
            String dateOfBirth, String dwn_img_url, String Mbl_no_2,
            String Email_1, String Email_2, String FB_Url,
            String Other_Url, String Home_City){

        dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(
                    studentName, studentId, studentMblNo,
                    dateOfBirth, dwn_img_url, Mbl_no_2,
                    Email_1, Email_2, FB_Url,
                    Other_Url, Home_City
            );
            toast = "Data sync is completed!";
        } catch (SQLiteException e){
            toast = "No new data found!";
        }
    }

    private void updateData (
            String studentName, String studentId, String studentMblNo,
            String dateOfBirth, String dwn_img_url, String Mbl_no_2,
            String Email_1, String Email_2, String FB_Url,
            String Other_Url, String Home_City) {

                dbHelper = new DBHelper(this);
                boolean updated = dbHelper.updateData(
                        studentName, studentId, studentMblNo,
                        dateOfBirth, dwn_img_url, Mbl_no_2, Email_1,
                        Email_2, FB_Url, Other_Url, Home_City
                );

                if (updated) {
                    //toast = "Data has been up to dated!";
                } else {
                    toast = "Date doesn't updated!";
                }
    }

    private void setNotification () {
        boolean alarmActive = (PendingIntent.getBroadcast(
                this,
                100,
                new Intent(this, NotificationReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmActive) {
            Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.MINUTE, 0);
            //calendar.set(Calendar.SECOND, 30);

            Intent intent = new Intent(this, NotificationReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    100,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }

    private void typeSMSDialog() {

        final Dialog myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.message_dialog);

        CardView cardViewMain = myDialog.findViewById(R.id.CardViewSMSMain);
        CardView cardViewSMSType = myDialog.findViewById(R.id.CardViewSMSType);
        TextView textView = myDialog.findViewById(R.id.TVTitleDialogSMS);
        final EditText editText = myDialog.findViewById(R.id.ETDialog);
        Button cancelBtn = myDialog.findViewById(R.id.BackButton);
        Button sendBtn = myDialog.findViewById(R.id.SendButton);

        if (theme.equals("Dark")) {
            cardViewMain.setCardBackgroundColor(getResources().getColor(R.color.dark_color_primary));
            cardViewSMSType.setCardBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
            textView.setTextColor(Color.WHITE);
            editText.setTextColor(Color.WHITE);
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.cancel();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() <= 0) {
                    Toast.makeText(MainActivity.this, "Type something to send.", Toast.LENGTH_LONG).show();
                } else {
                    sendSMStoAllWarningDialogs(editText.getText().toString());
                }
            }
        });

        myDialog.show();
    }

    private void sendSMStoAllWarningDialogs(final String msg) {
        AlertDialog.Builder builder;
        if (theme.equals("Dark")) {
            builder = new AlertDialog.Builder(this, R.style.MyDialogThemeDark);
            builder.setMessage(Html.fromHtml("<font color='#ffffff'>Do you want to send sms to all?</font>"));
            builder.setTitle(Html.fromHtml("<font color='#FFFF00'>Warning!!!</font>"));
        } else {
            builder = new AlertDialog.Builder(this);
            builder.setMessage(Html.fromHtml("<font color='#000000'>Do you want to send sms to all?</font>"));
            builder.setTitle(Html.fromHtml("<font color='#1DC4ED'>Warning!!!</font>"));
        }
        builder.setNegativeButton(Html.fromHtml("No"),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(Html.fromHtml("Yes"), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder;
                if (theme.equals("Dark")) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogThemeDark);
                    builder.setTitle(Html.fromHtml("<font color='#FFFF00'>This may result in additional charges!!</font>"));
                    builder.setMessage(Html.fromHtml("<font color='#ffffff'>Do you really want to do it?</font>"));
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(Html.fromHtml("<font color='#1DC4ED'>This may result in additional charges!!</font>"));
                    builder.setMessage(Html.fromHtml("<font color='#000000'>Do you really want to do it?</font>"));
                }
                builder.setNegativeButton(Html.fromHtml("No"),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(Html.fromHtml("Yes"),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmsManager sms = SmsManager.getDefault();
                        for (int i=0; i<mobileNoList.size(); i++) {
                            sms.sendTextMessage(mobileNoList.get(i), null, msg, null, null);
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void urlInputDialog() {
        final Dialog myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.input_url_dialog);

        CardView cardView1 = myDialog.findViewById(R.id.CardViewURL);
        CardView cardViewEt = myDialog.findViewById(R.id.CardView1);
        RelativeLayout rl = myDialog.findViewById(R.id.RLURL);
        final EditText urlEt = myDialog.findViewById(R.id.etLink);
        Button cancleBtn = myDialog.findViewById(R.id.CancelButton);
        Button okBtn = myDialog.findViewById(R.id.OkButton);

        if (theme.equals("Dark")) {
            cardView1.setCardBackgroundColor(getResources().getColor(R.color.dark_color_primary));
            rl.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
            cardViewEt.setCardBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
            urlEt.setTextColor(Color.WHITE);

        }

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlEt.getText().toString().trim().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter the url first!!", Toast.LENGTH_LONG).show();
                } else if (!urlEt.getText().toString().contains("https://")) {
                    Toast.makeText(getApplicationContext(), "Please Enter the actual URL address!!", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder;
                    if (theme.equals("Dark")) {
                        builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogThemeDark);
                        builder.setTitle(Html.fromHtml("<font color='#FFFF00'>Warning!! This URL going to replace default one.</font>"));
                        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure this is your Json URL?</font>"));
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(Html.fromHtml("<font color='#1DC4ED'>Warning!! This URL going to replace default one.</font>"));
                        builder.setMessage(Html.fromHtml("<font color='#000000'>Are you sure this is your Json URL?</font>"));
                    }
                    builder.setNegativeButton(Html.fromHtml("No"),new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton(Html.fromHtml("Yes"),new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                                    .putString("MY_URL", urlEt.getText().toString()).apply();
                            Toast.makeText(getApplicationContext(), "URL address saved!", Toast.LENGTH_LONG).show();
                            URL_LINK = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                                    .getString("MY_URL", "https://shhridoy.github.io/json/csteallstudent.js");
                            dialog.cancel();
                            myDialog.cancel();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.cancel();
            }
        });

        myDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            String path = params[0];
            int file_length = 0;
            try {
                URL url = new URL(path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                file_length = urlConnection.getContentLength();
                File new_folder = new File("sdcard/cste10nstu");
                if (!new_folder.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    new_folder.mkdir();
                }

                File input_file = new File(new_folder, setFileName(num+1) + ".jpg");
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                byte[] data = new byte[1024];

                int total = 0, count = 0;
                OutputStream outputStream = new FileOutputStream(input_file);

                while ( (count = inputStream.read(data)) != -1 ) {
                    total += count;
                    outputStream.write(data, 0, count);
                    int progress = (int) total * 100 / file_length;
                    publishProgress(progress);
                }

                inputStream.close();
                outputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Download complete";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Downloading image "+(num+1));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (num == LENGTH-1) {
                Toast.makeText(MainActivity.this,  "Download is completed.", Toast.LENGTH_LONG).show();
                loadRecyclerViewFromDatabase();
            }
            num++;
            imageDownload();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }
    }

    private void imageDownload() {
        if (num < LENGTH) {
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(URL_List.get(num));
        }
    }

    private String setFileName(int i) {
        dbHelper = new DBHelper(this);
        cursor = dbHelper.retrieveData();
        String fileName = "default";
        while (cursor.moveToNext()) {
            if (i == cursor.getInt(0)) {
                fileName = cursor.getString(2);
                break;
            }
        }
        return fileName;
    }

    private boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        getBaseContext();
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;
        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }

        return false;
    }

    @NonNull
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
                secondPart = "Jan";
                break;
            case 2:
                secondPart = "Feb";
                break;
            case 3:
                secondPart = "Mar";
                break;
            case 4:
                secondPart = "Apr";
                break;
            case 5:
                secondPart = "May";
                break;
            case 6:
                secondPart = "Jun";
                break;
            case 7:
                secondPart = "Jul";
                break;
            case 8:
                secondPart = "Aug";
                break;
            case 9:
                secondPart = "Sep";
                break;
            case 10:
                secondPart = "Oct";
                break;
            case 11:
                secondPart = "Nov";
                break;
            case 12:
                secondPart = "Dec";
                break;
            default:
                secondPart = "December";
        }

        return firstPart+", "+secondPart;
    }

    private void birthdayLists () {
        TextView tv01 = findViewById(R.id.TV01);
        TextView tv02 = findViewById(R.id.TV02);
        TextView tv03 = findViewById(R.id.TV03);
        TextView tv04 = findViewById(R.id.TV04);

        if (theme.equals("Dark")) {
            tv01.setTextColor(Color.WHITE);
            tv02.setTextColor(Color.WHITE);
            tv03.setTextColor(Color.WHITE);
            tv04.setTextColor(Color.WHITE);
        }

        ListView todayLv = findViewById(R.id.TodayBirthdayListView);
        ListView monthLv = findViewById(R.id.MonthBirthdayListView);
        ListView wentLv = findViewById(R.id.WentAwayListview);
        ArrayList<BirthdayListItems> list1 = new ArrayList<>();
        ArrayList<BirthdayListItems> list2 = new ArrayList<>();
        ArrayList<BirthdayListItems> list3 = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int currMonth = c.get(Calendar.MONTH)+1; // count month from 0 to 11
        int currDay = c.get(Calendar.DATE);

        String curr = stringFormatOfDate(String.valueOf(currDay), String.valueOf(currMonth));

        dbHelper = new DBHelper(this);
        cursor = dbHelper.retrieveDataInOrderToBirthdate();
        while (cursor.moveToNext()){
            String name = cursor.getString(1);
            String date = cursor.getString(4);
            String[] splitedDate = date.split("/");
            if (Integer.parseInt(splitedDate[1]) == currMonth) {
                if (Integer.parseInt(splitedDate[0]) == currDay) {
                    BirthdayListItems bl = new BirthdayListItems(name, stringFormatOfDate(splitedDate[0], splitedDate[1]));
                    list1.add(bl);
                } else if (Integer.parseInt(splitedDate[0]) > currDay) {
                    BirthdayListItems bl = new BirthdayListItems(name, stringFormatOfDate(splitedDate[0], splitedDate[1]));
                    list2.add(bl);
                } else {
                    BirthdayListItems bl = new BirthdayListItems(name, stringFormatOfDate(splitedDate[0], splitedDate[1]));
                    list3.add(bl);
                }
            }
        }

        if (list1.size() == 0) {
            BirthdayListItems bl = new BirthdayListItems("No birthday!!", curr);
            list1.add(bl);
        }

        if (list2.size() == 0) {
            BirthdayListItems bl = new BirthdayListItems("No birthday!!", "");
            list2.add(bl);
        }

        if (list3.size() == 0) {
            BirthdayListItems bl = new BirthdayListItems("No birthday!!", "");
            list2.add(bl);
        }

        CustomAdapter customAdapter1 = new CustomAdapter(this, 4, list1);
        todayLv.setAdapter(customAdapter1);

        CustomAdapter customAdapter2 = new CustomAdapter(this, 4, list2);
        monthLv.setAdapter(customAdapter2);

        CustomAdapter customAdapter3 = new CustomAdapter(this, 4, list3);
        wentLv.setAdapter(customAdapter3);

        ListUtils.setDynamicHeight(todayLv);
        ListUtils.setDynamicHeight(monthLv);
        ListUtils.setDynamicHeight(wentLv);

        AnimationUtil.bottomToUpAnimation(tv01, 500);
        AnimationUtil.bottomToUpAnimation(todayLv, 700);
        AnimationUtil.bottomToUpAnimation(tv02, 900);
        AnimationUtil.bottomToUpAnimation(tv03, 900);
        AnimationUtil.bottomToUpAnimation(monthLv, 1100);
        AnimationUtil.bottomToUpAnimation(tv04, 1300);
        AnimationUtil.bottomToUpAnimation(wentLv, 1500);

    }

    private void themesDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.theme_dialog);

        CardView cardView = dialog.findViewById(R.id.CardViewTheme);
        if (theme.equals("Dark")) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.dark_color_primary));
        }
        CircleMenu circleMenu = dialog.findViewById(R.id.CircleMenu);
        circleMenu.setMainMenu(getResources().getColor(R.color.indigo_400),
                R.drawable.ic_action_add,
                R.drawable.ic_action_remove)
                .addSubMenu(Color.WHITE, R.drawable.ic_action_sun_dark)
                .addSubMenu(getResources().getColor(R.color.dark_color_secondary), R.drawable.ic_action_cloud)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        if (index == 0) {
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                                    .putString("Theme", "White").apply();
                            Toast.makeText(getApplicationContext(), "Activating Light Mode...", Toast.LENGTH_LONG).show();
                        } else if (index == 1) {
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                                    .putString("Theme", "Dark").apply();
                            Toast.makeText(getApplicationContext(), "Activating Dark Mode...", Toast.LENGTH_LONG).show();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(getIntent());
                            }
                        }, 1500);
                    }
                });

        dialog.show();
    }

    private void changingTheme() {
        if (theme.equals("Dark")) {
            llContact.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
            rlMain.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
            scrollView.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
            toolbar.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
            toolbar.setPopupTheme(R.style.PopupMenuDark);
            llFavorite.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
            llBirthdays.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
            contactTv.setTextColor(Color.WHITE);
            favoriteTv.setTextColor(Color.WHITE);
            birthdayTv.setTextColor(Color.WHITE);
            footerTv.setTextColor(Color.WHITE);
            footerTv.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
        } else {
            llContact.setBackgroundColor(getResources().getColor(R.color.md_grey_300));
            llBirthdays.setBackgroundColor(getResources().getColor(R.color.indigo_400));
            llFavorite.setBackgroundColor(getResources().getColor(R.color.indigo_400));
            favoriteTv.setTextColor(Color.WHITE);
            birthdayTv.setTextColor(Color.WHITE);
        }
    }

    private void selectionFunction() {
        llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inFavorite = false;
                inBDlists = false;
                recyclerView.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.INVISIBLE);
                footerTv.setVisibility(View.INVISIBLE);
                if (theme.equals("Dark")) {
                    llContact.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
                    llBirthdays.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
                    llFavorite.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
                } else {
                    llContact.setBackgroundColor(getResources().getColor(R.color.md_grey_300));
                    llBirthdays.setBackgroundColor(getResources().getColor(R.color.indigo_400));
                    llFavorite.setBackgroundColor(getResources().getColor(R.color.indigo_400));
                    contactTv.setTextColor(Color.BLACK);
                    favoriteTv.setTextColor(Color.WHITE);
                    birthdayTv.setTextColor(Color.WHITE);
                }

                noData = dbHelper.retrieveData().getCount() == 0;

                /*if (noData) {
                    fab.show();
                    if (isInternetOn()) {
                        loadRecyclerViewFromJson();
                    } else {
                        Snackbar.make(findViewById(R.id.coordinatorMain), "Please turn your internet connection on to sync the data first time!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    fab.hide();
                    loadRecyclerViewFromDatabase();
                }*/

                if (isInternetOn()) {
                    loadRecyclerViewFromJson();
                } else {
                    fab.show();
                    loadRecyclerViewFromDatabase();
                }
            }
        });

        llFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noData = dbHelper.retrieveData().getCount() == 0;
                inFavorite = true;
                fab.hide();
                recyclerView.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.INVISIBLE);
                footerTv.setVisibility(View.INVISIBLE);
                if (theme.equals("Dark")) {
                    llFavorite.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
                    llBirthdays.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
                    llContact.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
                } else {
                    llFavorite.setBackgroundColor(getResources().getColor(R.color.md_grey_300));
                    llContact.setBackgroundColor(getResources().getColor(R.color.indigo_400));
                    llBirthdays.setBackgroundColor(getResources().getColor(R.color.indigo_400));
                    contactTv.setTextColor(Color.WHITE);
                    favoriteTv.setTextColor(Color.BLACK);
                    birthdayTv.setTextColor(Color.WHITE);
                }
                loadFavoriteRecyclerView();
            }
        });

        llBirthdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                inBDlists = true;
                recyclerView.setVisibility(View.INVISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                footerTv.setVisibility(View.VISIBLE);
                if (theme.equals("Dark")) {
                    llBirthdays.setBackgroundColor(getResources().getColor(R.color.dark_color_primary));
                    llContact.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
                    llFavorite.setBackgroundColor(getResources().getColor(R.color.dark_color_secondary));
                } else {
                    llBirthdays.setBackgroundColor(getResources().getColor(R.color.md_grey_300));
                    llContact.setBackgroundColor(getResources().getColor(R.color.indigo_400));
                    llFavorite.setBackgroundColor(getResources().getColor(R.color.indigo_400));
                    contactTv.setTextColor(Color.WHITE);
                    favoriteTv.setTextColor(Color.WHITE);
                    birthdayTv.setTextColor(Color.BLACK);
                }
                scrollView.scrollTo(0, 0);
                birthdayLists();
            }
        });
    }

    private  void checkPermissions() {

        /*if (checkPermissions()) {
            //  permissions  granted.
        }*/

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissionsList) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            //return false;
        }
        //return true;
    }

    private void callPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Call phone permission is necessary to make a call!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
                }
            }
        }
    }

    private void writeInSDCardPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write external storage permission is necessary to make directory for images!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_SD_PERMISSION_CODE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_SD_PERMISSION_CODE);
                }
            }
        }
    }

    private void smsPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write external storage permission is necessary to make directory for images!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                }
            }
        }
    }

}
