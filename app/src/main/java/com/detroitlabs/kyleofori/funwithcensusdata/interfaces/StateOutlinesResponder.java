package com.detroitlabs.kyleofori.funwithcensusdata.interfaces;

import com.detroitlabs.kyleofori.funwithcensusdata.model.Feature;
import java.util.ArrayList;

public interface StateOutlinesResponder {
  void onStateOutlinesReceived(ArrayList<Feature> features);
}
