package com.example.mihael.scanapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private static final String TEXT_TYPE = " TEXT";
    private static final String URL = "https://tranquil-mountain-52969.herokuapp.com/barcodes/";

    FeedReaderDbHelper mDbHelper;

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;
    private String TAG = "SplashScreen";

    private class GetDataFromAPIAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // log info
                Log.i(TAG, String.format("Connecting to %s", url.toString()));
                Log.i(TAG, String.format("HTTP Status Code: %d", connection.getResponseCode()));

                // if result differs from HTTP status code 200 (OK), quit
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                // get the result as String
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + '\n');
                }

                // show the result in log
                Log.i(TAG, String.format("GET: %s", stringBuilder.toString()));

                // return the result as JSONObject
                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "{}";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONArray json = null;

            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = (JSONObject) json.get(i);
                    String barcode = jsonObject.getString("barcode");
                    String qrcode = jsonObject.getString("qrCode");
                    try {
                        ContentValues values = new ContentValues();
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ENTRY_ID, barcode);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, qrcode);

                        long newRowId;
                        newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME,
                                null,
                                values);
                    } catch (Exception e) {

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mDbHelper = new FeedReaderDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        HashMap<String, String> data = DBImmitator.getInstance().getTranslator();

        if (isNetworkAvailable()) {
            fetchDataFromAPI();
        } else {
            for (String piece : data.keySet()) {
                try {
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ENTRY_ID, piece);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, data.get(piece));

                    long newRowId;
                    newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME,
                            null,
                            values);
                } catch (Exception e) {
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this, BarcodeScanActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void fetchDataFromAPI() {
        GetDataFromAPIAsyncTask task = new GetDataFromAPIAsyncTask();
        task.execute();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}