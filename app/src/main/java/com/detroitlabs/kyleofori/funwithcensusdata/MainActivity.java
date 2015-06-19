package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

public class MainActivity extends AppCompatActivity implements BoundaryDataReceiver.Receiver {

    public BoundaryDataReceiver boundaryDataReceiver;

    private static final LatLng NEWARK = new LatLng(40.714086, -74.228697);
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

        setUpMapIfNeeded();

        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, QueryService.class);
        intent.putExtra("receiver", boundaryDataReceiver);
        intent.putExtra("command", "query");
        startService(intent);

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWARK, 11));

        mImages.clear();
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922));
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_prudential_sunny));

        mCurrentEntry = 0;
        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
                .position(NEWARK, 8600f, 6500f));

        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(35, -90), new LatLng(36, -89), new LatLng(36, -81), new LatLng(35, -84))
                .strokeColor(Color.GREEN)
                .fillColor(Color.YELLOW));

    }

    public void switchImage(View view) {
        mCurrentEntry = (mCurrentEntry + 1) % mImages.size();
        mGroundOverlay.setImage(mImages.get(mCurrentEntry));
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryService.STATUS_RUNNING:
                break;
            case QueryService.STATUS_FINISHED:
                List results = resultData.getParcelableArrayList("results");
                break;
            case QueryService.STATUS_ERROR:
                break;
        }
    }
}
