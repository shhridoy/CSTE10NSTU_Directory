package com.cste10nstu.shhridoy.cste10nstu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MY_DATA = "https://shhridoy.github.io/json/csteallstudent.js";
    private static List<String> URL_List;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> itemsList;
    private String toast;
    private static int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        URL_List = new ArrayList<>();
        itemsList = new ArrayList<>();

        if (isInternetOn()) {
            loadRecyclerViewData();
        } else {
            populateDataBaseInRecyclerView();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetOn()) {
                    loadRecyclerViewData();
                } else {
                    Snackbar.make(view, "Please check you internet connection!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Toast.makeText(getApplicationContext(), "Please check you internet connection!!", Toast.LENGTH_LONG).show();
                }
            }
        });
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
                            JSONArray jsonArray = new JSONArray(response);

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

                                saveData(object.getString("name"),
                                        object.getString("id"),
                                        object.getString("mobile"));

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
                        populateDataBaseInRecyclerView();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
            if (num >= 55) {
                num = 0;
            }
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(URL_List.get(num));
            return true;

        } else if (id == R.id.menu_item_nextActivity) {
            startActivity(new Intent(this, SecondActivity.class));
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

    private void saveData(String studentName, String studentId, String studentMblNo){
        DBHelper dbHelper = new DBHelper(this);
        try{
            dbHelper.insertData(studentName, studentId, studentMblNo);
            toast = "Data sync is completed!";
        } catch (SQLiteException e){
            toast = "No update found!";
        }
    }

    private void updateData (String oldNo, String newNo) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.updateData(oldNo, newNo);
    }

    private void populateDataBaseInRecyclerView(){
        itemsList.clear();
        DBHelper databaseHelper = new DBHelper(this);
        Cursor c = databaseHelper.retrieveData();
        if(c.getCount() == 0){
            Toast.makeText(this, "Data doesn't sync yet!!",Toast.LENGTH_LONG).show();
        } else {
            while (c.moveToNext()){
                String name = c.getString(1);
                String st_id = c.getString(2);
                String st_mobile = c.getString(3);
                ListItems listItems = new ListItems(name, "ID: " +st_id, "CONTACT: "+st_mobile);
                itemsList.add(listItems);
                adapter = new MyAdapter(itemsList, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void imageDownload() {
        if (num < 55) {
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(URL_List.get(num));
        }
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

                File input_file = new File(new_folder, "image_cste" + num + ".jpg");
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
            if (num == 54) {
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

}
