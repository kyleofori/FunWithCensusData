package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.detroitlabs.kyleofori.funwithcensusdata.dialogs.OutsideClickDialogFragment;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.StateOutlinesResponder;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutlineCallMaker implements Callback<StatesModel> {

  public String clickedStateName;
  private MapClearingInterface mapClearingInterface;
  private MainActivity mainActivity;
  private StateOutlinesResponder stateOutlinesResponder;
  private Gson gson;
  private OutlinesModel model;

  public OutlineCallMaker(Context context) {
    mapClearingInterface = (MapClearingInterface) context;
    stateOutlinesResponder = (StateOutlinesResponder) context;
    mainActivity = (MainActivity) context;
  }

  protected void retrieveStateOutlines() {
    if(gson == null && model == null) {
      gson = new Gson();
      model = gson.fromJson(loadJsonStringFromAsset(), OutlinesModel.class);
    }
    stateOutlinesResponder.onStateOutlinesReceived(model);

  }

  private String loadJsonStringFromAsset() {
    String json;
    try {
      InputStream inputStream = mainActivity.getAssets().open("ERIC-CLST-Outline.json");
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      json = new String(buffer, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return json;
  }



  private void createOutsideClickDialogFragment(String message, String tag) {
    OutsideClickDialogFragment dialog = new OutsideClickDialogFragment();
    Bundle arguments = new Bundle();
    arguments.putCharSequence("message", message);
    dialog.setArguments(arguments);
    dialog.show(mainActivity.getFragmentManager(), tag);
  }

  private void resetStates() {
    mainActivity.getSelectedStateFragment().setFeature(null);
    mainActivity.getLocationName().setText(R.string.state_name_goes_here);
    clickedStateName = null;
  }

  //******************Methods for Callback<StatesModel>*****************//

  @Override public void onResponse(Call<StatesModel> call, Response<StatesModel> response) {
    if(response.body() == null) {
      Log.i("Hey-o", "response is null!!");
    } else {
      StatesModel statesModel = response.body();
      ArrayList<StatesModel.GoogleResult> results = statesModel.getResults();
      if (results.isEmpty()) {
        createOutsideClickDialogFragment(mainActivity.getString(R.string.body_of_water_message),
            "water");
      } else if (!statesModel.isInUSA()) {
        createOutsideClickDialogFragment(mainActivity.getString(R.string.other_land_message), "land");
      } else {
        ArrayList<StatesModel.GoogleResult.AddressComponent> addressComponents = results.get(0).getAddressComponents();
        for (StatesModel.GoogleResult.AddressComponent component : addressComponents) {
          ArrayList<String> types = component.getTypes();
          String firstType = types.get(0);
          if (firstType.equals(Constants.AA_LEVEL_1)) {
            clickedStateName = component.getLongName();
            if (mainActivity.getSelectedStateFragment().getFeature() == null) {
              retrieveStateOutlines();
            } else {
              mapClearingInterface.clearMap();
              if (clickedStateName.equals(mainActivity.getSelectedStateFragment()
                  .getFeature()
                  .getProperties()
                  .getPoliticalUnitName())) {
                resetStates();
              } else {
                retrieveStateOutlines();
              }
            }
          }
        }
      }
    }

  }

  @Override public void onFailure(Call<StatesModel> call, Throwable t) {
    t.printStackTrace();
  }
}
