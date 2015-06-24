package com.detroitlabs.kyleofori.funwithcensusdata;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyleofori on 6/19/15.
 */

public class QueryService extends IntentService {

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
                createPoints();
                b.putParcelableArrayList("results", (ArrayList<? extends Parcelable>) results);
                receiver.send(STATUS_FINISHED, b);
            } catch(Exception e) {
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
            }
        }
    }

    private void createPoints() {
        results.add(new LatLng(35, -90));
        results.add(new LatLng(36.6, -89));
        results.add(new LatLng(36.6, -81));
        results.add(new LatLng(35, -84));
    }
}
