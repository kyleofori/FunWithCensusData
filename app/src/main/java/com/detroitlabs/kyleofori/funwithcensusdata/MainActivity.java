package com.detroitlabs.kyleofori.funwithcensusdata;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.detroitlabs.kyleofori.funwithcensusdata.api.OutlinesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements Callback<OutlinesModel> {

    private static final LatLng USA_COORDINATES = new LatLng(39, -98);
    private static final int USA_ZOOM_LEVEL = 3;

    public int indexOfMostRecentPolygon = 0;

    private List<LatLng> points = new ArrayList<>();
    private List<List<LatLng>> polygonCollection = new ArrayList<>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializePoints();
        setUpMapIfNeeded();
        makeHttpCallWithRetrofit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void makeHttpCallWithRetrofit(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.API_BASE_URL)
                .build();

        OutlinesApi api = restAdapter.create(OutlinesApi.class);

        api.getOutlinesModel(MainActivity.this);
    }


    @Override
    public void success(OutlinesModel model, Response response) {
        ArrayList<OutlinesModel.Feature> features = model.getFeatures();
        OutlinesModel.Feature state = null;
        for(OutlinesModel.Feature feature: features) {
            if(feature.getProperties().getPoliticalUnitName().equals(Constants.AK)) {
                state = feature;
            }
        }
        OutlinesModel.Feature.Geometry geometry = state.getGeometry();
        Object coordinates = geometry.getCoordinates();

        if(geometry.getType().equals("Polygon")) {
            List<List<List<Double>>> polygonOutline = (ArrayList<List<List<Double>>>) coordinates;
            addEachPolygonToMap(polygonOutline);
        } else if (geometry.getType().equals("MultiPolygon")) {
            List<List<List<List<Double>>>> multiPolygonOutline = (ArrayList<List<List<List<Double>>>>) coordinates;
            for(List<List<List<Double>>> polygonOutline: multiPolygonOutline) {
                addEachPolygonToMap(polygonOutline);
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
    }


    private void initializePoints() {
        points.add(new LatLng(36.5, -89.4));
        points.add(new LatLng(39.0, -84.6));
        points.add(new LatLng(37.0, -82.7));
    }

    private void addEachPolygonToMap(List<List<List<Double>>> polygonOutline) {
        List<LatLng> polygon = makePolygonOfCoordinatePairs(polygonOutline);
        polygonCollection.add(polygon);
        addMostRecentPolygonToMap();
    }

    private List<LatLng> makePolygonOfCoordinatePairs(List<List<List<Double>>> polygonOutline) {
        List<LatLng> polygon = new ArrayList<>();
        for (List<Double> coordinatePair : polygonOutline.get(0)) {
            double lng = coordinatePair.get(0);
            double lat = coordinatePair.get(1);
            polygon.add(new LatLng(lat, lng));
        }
        return polygon;
    }

    private void addMostRecentPolygonToMap() {
        mMap.addPolygon(new PolygonOptions()
                .addAll(polygonCollection.get(indexOfMostRecentPolygon))
                .strokeColor(Color.BLACK)
                .strokeWidth(2)
                .fillColor(Color.WHITE));

        indexOfMostRecentPolygon++;
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(USA_COORDINATES, USA_ZOOM_LEVEL));

        mMap.addPolygon(new PolygonOptions()
            .addAll(points)
            .strokeColor(Color.GREEN)
            .strokeWidth(2)
            .fillColor(Color.YELLOW));
    }
}
