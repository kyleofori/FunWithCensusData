package com.detroitlabs.kyleofori.funwithcensusdata.interfaces;

import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import java.util.ArrayList;

public interface StateOutlinesResponder {
  void onStateOutlinesReceived(ArrayList<OutlinesModel.Feature> features);
  void onStateClicked(String clickedStateName);
}
