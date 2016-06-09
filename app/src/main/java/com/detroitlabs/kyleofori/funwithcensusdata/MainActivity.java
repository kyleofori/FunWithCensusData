package com.detroitlabs.kyleofori.funwithcensusdata;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import com.detroitlabs.kyleofori.funwithcensusdata.api.AcsSurveyApi;
import com.detroitlabs.kyleofori.funwithcensusdata.api.StatesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.SurveyDataResponder;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, GoogleMap.OnMapClickListener, Callback<OutlinesModel>,
    MapClearingInterface, SurveyDataResponder {

  public SelectedStateFragment selectedStateFragment;

  private static final LatLng USA_COORDINATES = new LatLng(39, -98);
  private static final int USA_ZOOM_LEVEL = 3;

  private List<List<LatLng>> polygonCollection = new ArrayList<>();
  public int indexOfMostRecentPolygon = 0;
  private OutlineCallMaker outlineCallMaker;
  private SlidingPanel popup;
  private GoogleMap map;
  private Animation animShow, animHide;
  public AcsSurveyModelCallback acsSurveyModelCallback;

  private TextView locationName;
  private TextView locationDescription;
  private ImageButton showButton;
  private ImageButton hideButton;
  private HashMap<String, String> statesHashMap;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }

  private void init() {
    setContentView(R.layout.activity_main);
    outlineCallMaker = new OutlineCallMaker(this);
    acsSurveyModelCallback = new AcsSurveyModelCallback(this);
    setUpMapIfNeeded();
    initPopup();
    initSelectedStateFragment();
  }

  private void initSelectedStateFragment() {
    FragmentManager manager = getSupportFragmentManager();
    selectedStateFragment = (SelectedStateFragment) manager.findFragmentByTag("selected_state");

    if (selectedStateFragment == null) {
      selectedStateFragment = new SelectedStateFragment();
      manager.beginTransaction().add(selectedStateFragment, "selected_state").commit();
    }

    if (selectedStateFragment.getFeature() != null) {
      highlightState(selectedStateFragment.getFeature());
      locationName.setText(selectedStateFragment.getFeature().getProperties().getPoliticalUnitName());
    }
    if (selectedStateFragment.getInformation() != null) {
      locationDescription.setText(selectedStateFragment.getInformation());
    }
  }

  @Override protected void onResume() {
    super.onResume();
    setUpMapIfNeeded();
    map.setOnMapClickListener(this);
  }

  @Override public void onMapClick(LatLng latLng) {
    makeHttpCallForStateNames(latLng);
  }

  protected void makeHttpCallForStateNames(LatLng latLng) {
    RestAdapter restAdapter =
        new RestAdapter.Builder().setEndpoint(Constants.GOOGLE_MAPS_API_BASE_URL).build();

    StatesApi statesApi = restAdapter.create(StatesApi.class);

    String latLngString = latLng.latitude + "," + latLng.longitude;

    statesApi.getStatesModel(latLngString, outlineCallMaker);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.show_popup_button:
        popup.setVisibility(View.VISIBLE);
        popup.startAnimation(animShow);
        showButton.setEnabled(false);
        hideButton.setEnabled(true);
        break;
      case R.id.hide_popup_button:
        popup.startAnimation(animHide);
        showButton.setEnabled(true);
        hideButton.setEnabled(false);
        popup.setVisibility(View.GONE);
        break;
    }
  }

  public TextView getLocationName() {
    return locationName;
  }

  public SelectedStateFragment getSelectedStateFragment() {
    return selectedStateFragment;
  }

  private void setUpMapIfNeeded() {
    if (map == null) {
      map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
      if (map != null) {
        setUpMap();
      }
    }
  }

  private void setUpMap() {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(USA_COORDINATES, USA_ZOOM_LEVEL));
  }

  private void initPopup() {
    popup = (SlidingPanel) findViewById(R.id.popup_window);
    popup.setVisibility(View.GONE);

    showButton = (ImageButton) findViewById(R.id.show_popup_button);
    showButton.setOnClickListener(this);
    hideButton = (ImageButton) findViewById(R.id.hide_popup_button);
    hideButton.setOnClickListener(this);

    animShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
    animHide = AnimationUtils.loadAnimation(this, R.anim.popup_hide);

    locationName = (TextView) findViewById(R.id.site_name);
    locationDescription = (TextView) findViewById(R.id.site_description);


    if (acsSurveyModelCallback.getVariable() != null) {
      locationDescription.setText(acsSurveyModelCallback.getVariable());
    } else {
      locationDescription.setText(R.string.state_information);
    }
  }

  private HashMap<String, String> createHashMap(ArrayList<OutlinesModel.Feature> features) {
    HashMap<String, String> hashMap = new HashMap<>();
    for(OutlinesModel.Feature feature: features) {
      hashMap.put(feature.getProperties().getPoliticalUnitName(), feature.getProperties().getStateNumber());
    }
    return hashMap;
  }

  private void makeHttpCallForAcsData(String stateName) {
    RestAdapter restAdapter =
        new RestAdapter.Builder().setEndpoint(Constants.ACS_2014_API_BASE_URL).build();

    AcsSurveyApi acsSurveyApi = restAdapter.create(AcsSurveyApi.class);
    Log.i("OutlineCallMaker", "made it here");


    acsSurveyApi.getAcsSurveyInformation("NAME,B01001B_007E", "state:" + statesHashMap.get(stateName), acsSurveyModelCallback);

  }

  private void highlightState(OutlinesModel.Feature state) {
    OutlinesModel.Feature.Geometry geometry = state.getGeometry();
    Object coordinates = geometry.getCoordinates();

    if (geometry.getType().equals(Constants.POLYGON)) {
      List<List<List<Double>>> polygonOutline = (ArrayList<List<List<Double>>>) coordinates;
      addEachPolygonToMap(polygonOutline);
    } else if (geometry.getType().equals(Constants.MULTIPOLYGON)) {
      List<List<List<List<Double>>>> multiPolygonOutline =
          (ArrayList<List<List<List<Double>>>>) coordinates;
      for (List<List<List<Double>>> polygonOutline : multiPolygonOutline) {
        addEachPolygonToMap(polygonOutline);
      }
    }
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
    map.addPolygon(new PolygonOptions().addAll(polygonCollection.get(indexOfMostRecentPolygon))
        .strokeColor(Color.BLACK)
        .strokeWidth(2)
        .fillColor(Color.WHITE));

    indexOfMostRecentPolygon++;
  }

  @Override public void clearMap() {
    map.clear();
  }

  @Override public void onAccessedSurveyData(String data) {
   locationDescription.setText(data);
    selectedStateFragment.setInformation(data);
  }

  //******************Methods for Callback<OutlinesModel>*****************//

  @Override public void success(OutlinesModel outlinesModel, Response response) {
    ArrayList<OutlinesModel.Feature> features = outlinesModel.getFeatures();
    OutlinesModel.Feature selectedState;
    if(statesHashMap == null) {
      statesHashMap = createHashMap(features);
    }
    for (OutlinesModel.Feature feature : features) { //TODO: don't assign state to feature in each iteration
      if (feature.getProperties().getPoliticalUnitName().equals(outlineCallMaker.clickedStateName)) {
        selectedState = feature;
        selectedStateFragment.setFeature(selectedState);
        highlightState(selectedState);
        locationName.setText(selectedState.getProperties().getPoliticalUnitName());
        makeHttpCallForAcsData(selectedState.getProperties().getPoliticalUnitName());
      }
    }
  }

  @Override public void failure(RetrofitError error) {
    error.printStackTrace();
  }
}
