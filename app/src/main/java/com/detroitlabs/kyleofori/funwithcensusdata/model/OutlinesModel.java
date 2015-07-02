package com.detroitlabs.kyleofori.funwithcensusdata.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by kyleofori on 7/1/15.
 */
public class OutlinesModel {

    @Expose
    private ArrayList<LatLng> results;

    public ArrayList<LatLng> getResults() {
        return results;
    }

    public void setResults(ArrayList<LatLng> results) {
        this.results = results;
    }
}
