package com.detroitlabs.kyleofori.funwithcensusdata;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyleofori on 6/19/15.
 */

public class QueryService extends IntentService {

    public static final String LOG_TAG = QueryService.class.getSimpleName();
    final static int DC = 5;
    final static String API = "http://eric.clst.org";
    final static int STATUS_FINISHED = 1;
    final static int STATUS_RUNNING = 0;
    final static int STATUS_ERROR = -1;
    private List<LatLng> results = new ArrayList<>();

    public QueryService() {
        super("Query Service");
    }

    public QueryService(String name, List<LatLng> results) {
        super(name);
        this.results = results;
    }

    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle b = new Bundle();
        if(command.equals("query")) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                // get some data or something. I think JSON data would be pulled and parsed here.
                makeHttpCall();
                createPoints();
                b.putParcelableArrayList("results", (ArrayList<? extends Parcelable>) results);
                receiver.send(STATUS_FINISHED, b);
            } catch(Exception e) {
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
            }
        }
    }

    protected void makeHttpCall() {

        HttpURLConnection httpURLConnection = null;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("eric.clst.org")
                .appendPath("wupl")
                .appendPath("Stuff")
                .appendPath("gz_2010_us_040_00_20m.json");

        String usStatesGeoJsonUrl = builder.build().toString();
        Log.i("QueryService", usStatesGeoJsonUrl);

        URL url;
        try {
            url = new URL(usStatesGeoJsonUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            String jsonString = readStream(in);
            Log.i(LOG_TAG, jsonString);
            parseJsonString(jsonString);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Something's wrong with the URL.");
        } catch (IOException e) {
            Log.e(LOG_TAG, "There was a problem connecting to the URL or reading the input.");
        } finally {
            httpURLConnection.disconnect();
        }
    }

    private void parseJsonString(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray features = jsonObject.getJSONArray("features");
            JSONObject stateOfArizona = features.getJSONObject(DC);
            JSONObject geometry = stateOfArizona.getJSONObject("geometry");
            JSONArray coordinatesl3 = geometry.getJSONArray("coordinates");
            JSONArray coordinatesl2 = coordinatesl3.getJSONArray(0);
            for(int i = 0; i < coordinatesl2.length(); i++) {
                double[] coords = (double[]) coordinatesl2.get(i);
                double latitude = coords[0];
                double longitude = coords[1];
                results.add(new LatLng(latitude, longitude));
            }

            //index 22, make sure to keep track of how deep arrays go

        } catch (JSONException e) {
            Log.e(LOG_TAG, "There was a JSON Exception");
        }


    }

    private String readStream(InputStream in) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "There was a problem building the JSON String.");
        }

        return stringBuilder.toString();
    }

    private void createPoints() {
        results.add(new LatLng(35, -90));
        results.add(new LatLng(36.6, -89));
        results.add(new LatLng(36.6, -81));
        results.add(new LatLng(35, -84));
    }
}
