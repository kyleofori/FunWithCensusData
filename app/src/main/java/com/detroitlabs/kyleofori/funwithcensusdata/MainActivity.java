package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.detroitlabs.kyleofori.funwithcensusdata.api_interfaces.AcsSurveyApi;
import com.detroitlabs.kyleofori.funwithcensusdata.api_interfaces.StatesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.MapClearer;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.StateOutlinesResponder;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.SurveyDataResponder;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.security.ProviderInstaller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
    implements GoogleMap.OnMapClickListener, MapClearer, SurveyDataResponder,
    StateOutlinesResponder, ProviderInstaller.ProviderInstallListener {

  private static final LatLng USA_COORDINATES = new LatLng(39, -98);
  private static final int USA_ZOOM_LEVEL = 3;
  private static final int ERROR_DIALOG_REQUEST_CODE = 1;
  private static final String NO_PROVIDER_TAG = "No provider available";

  public SelectedStateFragment selectedStateFragment;
  public AcsSurveyModelCallback acsSurveyModelCallback;
  public int indexOfMostRecentPolygon = 0;

  private List<List<LatLng>> polygonCollection = new ArrayList<>();
  private StatesModelCallback statesModelCallback;
  private GoogleMap map;
  private TextView locationName;
  private TextView locationDescription;
  private HashMap<String, String> statesHashMap;
  private BottomSheetBehavior bottomSheetBehavior;
  private boolean retryProviderInstall;

  @Override public void onMapClick(LatLng latLng) {
    makeHttpCallForStateNames(latLng);
  }

  @Override public void clearMap() {
    map.clear();
  }

  @Override public void onAccessedSurveyData(String data) {
    locationDescription.setText(data);
    selectedStateFragment.setInformation(data);
  }

  @Override public void onStateOutlinesReceived(OutlinesModel model) {
    ArrayList<OutlinesModel.Feature> features = model.getFeatures();
    OutlinesModel.Feature selectedState;
    if (statesHashMap == null) {
      statesHashMap = createHashMap(features);
    }
    for (OutlinesModel.Feature feature : features) {
      if (feature.getProperties()
          .getPoliticalUnitName()
          .equals(statesModelCallback.clickedStateName)) {
        selectedState = feature;
        selectedStateFragment.setFeature(selectedState);
        highlightState(selectedState);
        locationName.setText(selectedState.getProperties().getPoliticalUnitName());
        makeHttpCallForAcsData(selectedState.getProperties().getPoliticalUnitName());
      }
    }
    toggleBottomSheet();
  }

  @Override public void onProviderInstalled() {

  }

  @Override public void onProviderInstallFailed(int errorCode, Intent intent) {
    GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
    if (availability.isUserResolvableError(errorCode)) {
      availability.showErrorDialogFragment(this, errorCode, ERROR_DIALOG_REQUEST_CODE, new DialogInterface.OnCancelListener() {
        @Override public void onCancel(DialogInterface dialog) {
          onProviderInstallerNotAvailable();
        }
      });
    } else {
      onProviderInstallerNotAvailable();
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }

  @Override protected void onResume() {
    super.onResume();
    setUpMapIfNeeded();
    map.setOnMapClickListener(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ERROR_DIALOG_REQUEST_CODE) {
      retryProviderInstall = true;
    }
  }

  @Override protected void onPostResume() {
    super.onPostResume();
    if (retryProviderInstall) {
      ProviderInstaller.installIfNeededAsync(this, this);
    }
    retryProviderInstall = false;
  }

  public TextView getLocationName() {
    return locationName;
  }

  public SelectedStateFragment getSelectedStateFragment() {
    return selectedStateFragment;
  }

  protected void makeHttpCallForStateNames(LatLng latLng) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.GOOGLE_MAPS_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    StatesApi statesApi = retrofit.create(StatesApi.class);

    String latLngString = latLng.latitude + "," + latLng.longitude;
    Call<StatesModel> statesModelCall = statesApi.getStatesModel(latLngString);
    statesModelCall.enqueue(statesModelCallback);
  }

  private void init() {
    ProviderInstaller.installIfNeededAsync(this, this);
    setContentView(R.layout.activity_main);
    statesModelCallback = new StatesModelCallback(this);
    acsSurveyModelCallback = new AcsSurveyModelCallback(this);
    setUpMapIfNeeded();
    initBottomSheetText();
    initSelectedStateFragment();
    View bottomSheet = findViewById(R.id.bottom_sheet);
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
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

  private void initBottomSheetText() {
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
    for (OutlinesModel.Feature feature : features) {
      hashMap.put(feature.getProperties().getPoliticalUnitName(),
          feature.getProperties().getStateNumber());
    }
    return hashMap;
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
      locationName.setText(
          selectedStateFragment.getFeature().getProperties().getPoliticalUnitName());
    }
    if (selectedStateFragment.getInformation() != null) {
      locationDescription.setText(selectedStateFragment.getInformation());
    }
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

  private void makeHttpCallForAcsData(String stateName) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.ACS_2014_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    AcsSurveyApi acsSurveyApi = retrofit.create(AcsSurveyApi.class);

    Call<ArrayList<ArrayList<String>>> call =
        acsSurveyApi.getAcsSurveyInformation("NAME,B01001B_007E",
            "state:" + statesHashMap.get(stateName));
    call.enqueue(acsSurveyModelCallback);
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

  private void toggleBottomSheet() {
    if (selectedStateFragment.getFeature() == null) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    } else {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
  }

  private void onProviderInstallerNotAvailable() {
    //We will have to consider all HTTP communication as vulnerable.
    Log.i(NO_PROVIDER_TAG, "All HTTP communication is vulnerable because the security provider could"
        + "not be installed.");
  }
}

