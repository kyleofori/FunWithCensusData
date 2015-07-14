package com.detroitlabs.kyleofori.funwithcensusdata;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.detroitlabs.kyleofori.funwithcensusdata.api.OutlinesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements BoundaryDataReceiver.Receiver {

    public static final String API_BASE_URL = "http://eric.clst.org";
    public static final String DC = "District of Columbia";
    public static final String IA = "Iowa";
    public static final String MI = "Michigan";

    public BoundaryDataReceiver boundaryDataReceiver;

    private List<LatLng> points = new ArrayList<>();
    private List<LatLng> addedPoints = new ArrayList<>();

    private boolean firstPassDone = false;

    private static final LatLng TENNESSEE = new LatLng(35, -90);
    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();

    private GoogleMap mMap;
    private GroundOverlay mGroundOverlay;

    private int mCurrentEntry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boundaryDataReceiver = new BoundaryDataReceiver(new Handler());
        boundaryDataReceiver.setReceiver(this);

        setContentView(R.layout.activity_main);

        initializePoints();

        setUpMapIfNeeded();

        makeHttpCallWithRetrofit();

//        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryService.class);
//        intent.putExtra("receiver", boundaryDataReceiver);
//        intent.putExtra("command", "query");
//        startService(intent);

    }

    private void initializePoints() {
        points.add(new LatLng(36.5, -89.4));
        points.add(new LatLng(39.0, -84.6));
        points.add(new LatLng(37.0, -82.7));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boundaryDataReceiver.setReceiver(null);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryService.STATUS_RUNNING:
                break;
            case QueryService.STATUS_FINISHED:
                points = resultData.getParcelableArrayList("results");
                setUpMap();
                break;
            case QueryService.STATUS_ERROR:
                break;
        }
    }

    protected void makeHttpCallWithRetrofit(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_BASE_URL)
                .build();

        OutlinesApi api = restAdapter.create(OutlinesApi.class);

        Callback callback = new Callback<OutlinesModel>() {
            @Override
            public void success(OutlinesModel model, Response response) {
                ArrayList<OutlinesModel.Feature> features = model.getFeatures();
                OutlinesModel.Feature featureINeed = null;
                for(OutlinesModel.Feature f: features) {
                    if(f.getProperties().getPoliticalUnitName().equals(MI)) {
                        featureINeed = f;
                    }
                }
                OutlinesModel.Feature.Geometry geometry = featureINeed.getGeometry();
                Object coordinates = geometry.getCoordinates();

                if(geometry.getType().equals("Polygon")) {
                    List<List<List<Double>>> polygonOutline = (ArrayList<List<List<Double>>>) coordinates;

                    for (List<Double> coordinatePair : polygonOutline.get(0)) {
                        double lng = coordinatePair.get(0);
                        double lat = coordinatePair.get(1);
                        addedPoints.add(new LatLng(lat, lng));
                    }

                    setUpMap();
                } else if (geometry.getType().equals("MultiPolygon")) {
                    List<List<List<List<Double>>>> multiPolygonOutline = (ArrayList<List<List<List<Double>>>>) coordinates;

                    for(List<List<List<Double>>> polygonOutline: multiPolygonOutline) {
                        for (List<Double> coordinatePair : polygonOutline.get(0)) {
                            double lng = coordinatePair.get(0);
                            double lat = coordinatePair.get(1);
                            addedPoints.add(new LatLng(lat, lng));
                        }

                        setUpMap();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        };

        api.getOutlinesModel(callback);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TENNESSEE, 5));

        mImages.clear();
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922));
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_prudential_sunny));

        mCurrentEntry = 0;
        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
                .position(TENNESSEE, 8600f, 6500f));

        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .addAll(points)
                .strokeColor(Color.GREEN)
                .strokeWidth(2)
                .fillColor(Color.YELLOW));

        if(firstPassDone) {
            Polygon dcPolygon = mMap.addPolygon(new PolygonOptions()
                    .addAll(addedPoints)
                    .strokeColor(Color.RED)
                    .strokeWidth(2)
                    .fillColor(Color.MAGENTA));
        }

        firstPassDone = true;
    }

    public void switchImage(View view) {
        mCurrentEntry = (mCurrentEntry + 1) % mImages.size();
        mGroundOverlay.setImage(mImages.get(mCurrentEntry));
    }
}
