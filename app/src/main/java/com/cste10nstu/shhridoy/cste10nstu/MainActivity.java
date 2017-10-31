package com.cste10nstu.shhridoy.cste10nstu;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cste10nstu.shhridoy.cste10nstu.MyDatabase.DBHelper;
import com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData.ListItems;
import com.cste10nstu.shhridoy.cste10nstu.RecyclerViewData.MyAdapter;

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

    private static final String MY_DATA = "https://shhridoy.github.io/json/csteallstudent.js";
    private static List<String> URL_List;
    private static int num = 0;
    private static int LENGTH;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> itemsList;
    private String toast;
    private List<String> mobileNoList;
    FloatingActionButton fab;

    private DBHelper dbHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);
        fab.hide();

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fab.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fab.hide();
                    }
                }, 6000);
                return false;
            }
        });

        URL_List = new ArrayList<>();
        itemsList = new ArrayList<>();
        mobileNoList = new ArrayList<>();

        dbHelper = new DBHelper(this);
        if (dbHelper.retrieveData().getCount() == 0) {
            if (isInternetOn()) {
                loadRecyclerViewData();
            } else {
                Snackbar.make(findViewById(R.id.coordinatorMain), "Please turn your internet connection on to sync the data first time!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else {
            populateRecyclerViewFromDatabase();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetOn()) {
                    loadRecyclerViewData();
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorMain), "Please check you internet connection!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

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
                    adapter = new MyAdapter(filteredList, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();  // data set changed
                    return false;
                }
            });

            return true;

        } else if (id == R.id.menu_item_downloadImage) {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle(Html.fromHtml("<font color='#1DC4ED'>Warning!!</font>"));
            builder.setMessage(Html.fromHtml("<font color='#000000'>Do you want to download images for offline?</font>"));
            builder.setNegativeButton(Html.fromHtml("No"),new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                }
            });
            builder.setPositiveButton(Html.fromHtml("Yes"),new DialogInterface.OnClickListener() {

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
            typeSMSdialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    boolean backButoonPressedOnce = false;
    @Override
    public void onBackPressed() {
        if(backButoonPressedOnce){
            super.onBackPressed();
            return;
        }

        this.backButoonPressedOnce = true;
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorMain), "Please Press Back Again to Exit", Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backButoonPressedOnce = false;
            }
        }, 2000);
    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, MY_DATA,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

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

                                itemsList.add(list);
                                adapter = new MyAdapter(itemsList, getApplicationContext());
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        populateRecyclerViewFromDatabase();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void populateRecyclerViewFromDatabase(){
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
                adapter = new MyAdapter(itemsList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                i++;
            }
            LENGTH = i;
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
            int id,
            String studentName, String studentId, String studentMblNo,
            String dateOfBirth, String dwn_img_url, String Mbl_no_2,
            String Email_1, String Email_2, String FB_Url,
            String Other_Url, String Home_City) {

                dbHelper = new DBHelper(this);
                boolean updated = dbHelper.updateData(
                        id, studentName, studentId, studentMblNo,
                        dateOfBirth, dwn_img_url, Mbl_no_2, Email_1,
                        Email_2, FB_Url, Other_Url, Home_City
                );

                if (updated) {
                    toast = "Data has been up to date!";
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

    private void typeSMSdialog () {
        final Dialog myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.message_dialog);

        RelativeLayout relativeLayout = myDialog.findViewById(R.id.RLDIalog);
        TextView textView = myDialog.findViewById(R.id.TVTitleDialog);
        final EditText editText = myDialog.findViewById(R.id.ETDialog);
        Button cancelBtn = myDialog.findViewById(R.id.BackButton);
        Button sendBtn = myDialog.findViewById(R.id.SendButton);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Html.fromHtml("<font color='#1DC4ED'>Warning!</font>"));
        builder.setMessage(Html.fromHtml("<font color='#000000'>Do you want to send sms to all?</font>"));
        builder.setNegativeButton(Html.fromHtml("No"),new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        builder.setPositiveButton(Html.fromHtml("Yes"), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(Html.fromHtml("<font color='#1DC4ED'>This may result in additional charges!!</font>"));
                builder.setMessage(Html.fromHtml("<font color='#000000'>Do you really want to do it?</font>"));
                builder.setNegativeButton(Html.fromHtml("No"),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(Html.fromHtml("Yes"),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
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
                fileName = cursor.getString(1);
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
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING &&
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;
        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }

        return false;
    }

}
