package com.detroitlabs.kyleofori.funwithcensusdata.model;

import java.util.ArrayList;

public class StatesModel {
  private ArrayList<GoogleResult> results;

  public ArrayList<GoogleResult> getResults() {
    return results;
  }

  public boolean isInUSA() {
    for (StatesModel.GoogleResult result : results) {
      if (result.isInUSA()) {
        return true;
      }
    }
    return false;
  }

  public class GoogleResult {
    private ArrayList<AddressComponent> address_components;

    public ArrayList<AddressComponent> getAddressComponents() {
      return address_components;
    }

    public boolean isInUSA() {
      for (AddressComponent addressComponent : address_components) {
        if (addressComponent.isInUSA()) {
          return true;
        }
      }
      return false;
    }

    public class AddressComponent {
      private String long_name;

      private String short_name;

      private ArrayList<String> types;

      public String getLongName() {
        return long_name;
      }

      public String getShortName() {
        return short_name;
      }

      public ArrayList<String> getTypes() {
        return types;
      }

      public boolean isInUSA() {
        return getShortName().equals("US");
      }
    }
  }
}
