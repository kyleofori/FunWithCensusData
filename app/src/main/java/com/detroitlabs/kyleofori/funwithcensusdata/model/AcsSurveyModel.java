package com.detroitlabs.kyleofori.funwithcensusdata.model;

import java.util.ArrayList;

public class AcsSurveyModel {
  private ArrayList<String> listOfStrings;

  public ArrayList<String> getListOfStrings() {
    return listOfStrings;
  }

  public String getStateName() {
    return listOfStrings.get(0);
  }

  public String getValue() {
    return listOfStrings.get(1);
  }

  public String getStateNumber() {
    return listOfStrings.get(2);
  }

}
