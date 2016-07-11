package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.detroitlabs.kyleofori.funwithcensusdata.api_interfaces.AcsSurveyApi;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.StateOutlinesResponder;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.SurveyDataResponder;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
    implements SurveyDataResponder, StateOutlinesResponder {

  public SelectedStateFragment selectedStateFragment;
  public AcsSurveyModelCallback acsSurveyModelCallback;

  private MapController mapController;
  private TextView locationName;
  private TextView locationDescription;
  private BottomSheetBehavior bottomSheetBehavior;
  private String variableName;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }

  @Override protected void onResume() {
    super.onResume();
    mapController.setUpMapIfNeeded();
  }

  @Override public void onAccessedSurveyData(String data) {
    locationDescription.setText(data);
    selectedStateFragment.setInformation(data);
  }

  @Override public void onStateOutlinesReceived(ArrayList<OutlinesModel.Feature> features) {
    if (selectedStateFragment.getStatesHashMap() == null) {
      selectedStateFragment.setStatesHashMap(createHashMap(features));
    }
    selectedStateFragment.setAllFeatures(features);
    selectedStateFragment.setFeaturesLoaded(true);
  }

  @Override public void onStateClicked(String clickedStateName) {
    if(selectedStateFragment.areFeaturesLoaded()) {
      updateUiForClickedState(clickedStateName);
      toggleBottomSheet();
    }
  }

  private void updateUiForClickedState(String clickedStateName) {
    if (clickedStateName
        .equals(mapController.getStatesModelCallback().clickedStateName)) {
      for(OutlinesModel.Feature feature: selectedStateFragment.getAllFeatures()) {
        if(feature.getProperties().getPoliticalUnitName().equals(clickedStateName)) {
          selectedStateFragment.setFeature(feature);
          mapController.highlightState(feature);
          locationName.setText(feature.getProperties().getPoliticalUnitName());
          makeHttpCallForAcsData(feature.getProperties().getPoliticalUnitName());
        }
      }
    }
  }

  public TextView getLocationName() {
    return locationName;
  }

  public SelectedStateFragment getSelectedStateFragment() {
    return selectedStateFragment;
  }

  private void init() {
    variableName = getVariableNameFromIntent();
    setContentView(R.layout.activity_main);
    acsSurveyModelCallback = new AcsSurveyModelCallback(this);
    initBottomSheetText();
    mapController = new MapController(this);
    mapController.init();
    initSelectedStateFragment();
    View bottomSheet = findViewById(R.id.bottom_sheet);
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
  }

  private String getVariableNameFromIntent() {
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();
    return (String) bundle.get(SplashActivity.VAR_NAME);
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

    if (selectedStateFragment.getAllFeatures() == null) {
      new GetStateOutlinesTask().execute(this);
    }

    if (selectedStateFragment.getFeature() != null) {
      mapController.highlightState(selectedStateFragment.getFeature());
      locationName.setText(
          selectedStateFragment.getFeature().getProperties().getPoliticalUnitName());
    }
    if (selectedStateFragment.getInformation() != null) {
      locationDescription.setText(selectedStateFragment.getInformation());
    }
  }

  private void makeHttpCallForAcsData(String stateName) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.ACS_2014_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    AcsSurveyApi acsSurveyApi = retrofit.create(AcsSurveyApi.class);

    Call<ArrayList<ArrayList<String>>> call =
        acsSurveyApi.getAcsSurveyInformation(variableName,
            "state:" + selectedStateFragment.getStatesHashMap().get(stateName));
    call.enqueue(acsSurveyModelCallback);
  }

  private void toggleBottomSheet() {
    if (selectedStateFragment.getFeature() == null) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    } else {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
  }
}