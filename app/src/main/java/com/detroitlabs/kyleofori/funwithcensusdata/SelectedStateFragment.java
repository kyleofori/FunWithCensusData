package com.detroitlabs.kyleofori.funwithcensusdata;

import android.os.Bundle;
import com.detroitlabs.kyleofori.funwithcensusdata.model.Feature;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectedStateFragment extends android.support.v4.app.Fragment {
  private ArrayList<Feature> allFeatures;
  private Feature feature;
  private String information;
  private HashMap<String, String> statesHashMap;
  private boolean featuresLoaded;


  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  public void setFeature(Feature feature) {
    this.feature = feature;
  }

  public Feature getFeature() {
    return feature;
  }

  public void setInformation(String information) {
    this.information = information;
  }

  public String getInformation() {
    return information;
  }


  public void setAllFeatures(ArrayList<Feature> allFeatures) {
    this.allFeatures = allFeatures;
  }

  public ArrayList<Feature> getAllFeatures() {
    return allFeatures;
  }

  public HashMap<String, String> getStatesHashMap() {
    return statesHashMap;
  }

  public void setStatesHashMap(HashMap<String, String> statesHashMap) {
    this.statesHashMap = statesHashMap;
  }

  public boolean areFeaturesLoaded() {
    return featuresLoaded;
  }

  public void setFeaturesLoaded(boolean featuresLoaded) {
    this.featuresLoaded = featuresLoaded;
  }
}
