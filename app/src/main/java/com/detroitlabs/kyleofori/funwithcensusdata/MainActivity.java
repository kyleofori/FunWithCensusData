package com.detroitlabs.kyleofori.funwithcensusdata;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.detroitlabs.kyleofori.funwithcensusdata.api.OutlinesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.api.StatesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;
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

public class MainActivity extends AppCompatActivity implements Callback<OutlinesModel>, GoogleMap.OnMapLongClickListener {

    private Animation animShow, animHide;
    private static final LatLng USA_COORDINATES = new LatLng(39, -98);
    private static final int USA_ZOOM_LEVEL = 3;

    public int indexOfMostRecentPolygon = 0;

    private List<LatLng> points = new ArrayList<>();
    private List<List<LatLng>> polygonCollection = new ArrayList<>();
    private GoogleMap map;
    private String clickedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializePoints();
        setUpMapIfNeeded();
        initPopup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        map.setOnMapLongClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void makeHttpCallForStateOutlines(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.ERIC_CLST_API_BASE_URL)
                .build();

        OutlinesApi api = restAdapter.create(OutlinesApi.class);

        api.getOutlinesModel(this);
    }

    protected void makeHttpCallForStateNames(String latLngString){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.GOOGLE_MAPS_API_BASE_URL)
                .build();

        StatesApi api = restAdapter.create(StatesApi.class);

        Callback<StatesModel> callback = new Callback<StatesModel>() {
            @Override
            public void success(StatesModel statesModel, Response response) {
                ArrayList<StatesModel.GoogleResult> results = statesModel.getResults();
                ArrayList<StatesModel.GoogleResult.AddressComponent> addressComponents = results.get(0).getAddressComponents();
                String stateName;
                for(StatesModel.GoogleResult.AddressComponent component: addressComponents) {
                    if (component.getTypes().get(0).equals(Constants.AA_LEVEL_1)) {
                        stateName = component.getLongName();
                        clickedState = stateName;
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };

        api.getStatesModel(latLngString, callback);
    }

    @Override
    public void success(OutlinesModel model, Response response) {
        ArrayList<OutlinesModel.Feature> features = model.getFeatures();
        OutlinesModel.Feature state = null;
        for(OutlinesModel.Feature feature: features) {
            if(feature.getProperties().getPoliticalUnitName().equals(clickedState)) {
                state = feature;
            }
        }
        OutlinesModel.Feature.Geometry geometry = state.getGeometry();
        Object coordinates = geometry.getCoordinates();

        if(geometry.getType().equals(Constants.POLYGON)) {
            List<List<List<Double>>> polygonOutline = (ArrayList<List<List<Double>>>) coordinates;
            addEachPolygonToMap(polygonOutline);
        } else if (geometry.getType().equals(Constants.MULTIPOLYGON)) {
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

    @Override
    public void onMapLongClick(LatLng latLng) {
        String latLngString = latLng.latitude + "," + latLng.longitude;
        makeHttpCallForStateNames(latLngString);
        makeHttpCallForStateOutlines();
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
        map.addPolygon(new PolygonOptions()
                .addAll(polygonCollection.get(indexOfMostRecentPolygon))
                .strokeColor(Color.BLACK)
                .strokeWidth(2)
                .fillColor(Color.WHITE));

        indexOfMostRecentPolygon++;
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(USA_COORDINATES, USA_ZOOM_LEVEL));

        map.addPolygon(new PolygonOptions()
                .addAll(points)
                .strokeColor(Color.GREEN)
                .strokeWidth(2)
                .fillColor(Color.YELLOW));
    }

    private void initPopup() {

        final SlidingPanel popup = (SlidingPanel) findViewById(R.id.popup_window);

        // <span id="IL_AD2" class="IL_AD">Hide</span> the popup initially.....
        popup.setVisibility(View.GONE);

        animShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.popup_hide);

        final ImageButton showButton = (ImageButton) findViewById(R.id.show_popup_button);
        final ImageButton   hideButton = (ImageButton) findViewById(R.id.hide_popup_button);
        showButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                popup.setVisibility(View.VISIBLE);
                popup.startAnimation(animShow);
                showButton.setEnabled(false);
                hideButton.setEnabled(true);
            }
        });

        hideButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                popup.startAnimation( animHide );
                showButton.setEnabled(true);
                hideButton.setEnabled(false);
                popup.setVisibility(View.GONE);
            }});

        final TextView locationName = (TextView) findViewById(R.id.site_name);
        final TextView locationDescription = (TextView) findViewById(R.id.site_description);

        locationName.setText("CoderzHeaven");
        locationDescription.setText("Heaven of all working codes"
                + " A place where you can ask, share & even shout for code! Let’s share a wide range of technology here." +
                " From this site you will get a lot of working examples in your favorite programming languages!." +
                " Always remember we are only one comment away from you… Let’s shorten the distance between your doubts and your answers…");

    }
}
