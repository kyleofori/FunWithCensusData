package com.detroitlabs.kyleofori.funwithcensusdata;

import android.os.Bundle;
import com.detroitlabs.kyleofori.funwithcensusdata.dialogs.OutsideClickDialogFragment;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.MapClearer;
import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatesModelCallback implements Callback<StatesModel> {

  public String clickedStateName;
  private MapClearer mapClearer;
  private MainActivity mainActivity;

  public StatesModelCallback(MapClearer mapClearer, MainActivity mainActivity) {
    this.mapClearer = mapClearer;
    this.mainActivity = mainActivity;
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

  @Override public void onResponse(Call<StatesModel> call, Response<StatesModel> response) {
    if (response.body() == null) {
    } else {
      StatesModel statesModel = response.body();
      ArrayList<StatesModel.GoogleResult> results = statesModel.getResults();
      if (results.isEmpty()) {
        createOutsideClickDialogFragment(mainActivity.getString(R.string.body_of_water_message),
            "water");
      } else if (!statesModel.isInUSA()) {
        createOutsideClickDialogFragment(mainActivity.getString(R.string.other_land_message),
            "land");
      } else {
        ArrayList<StatesModel.GoogleResult.AddressComponent> addressComponents =
            results.get(0).getAddressComponents();
        for (StatesModel.GoogleResult.AddressComponent component : addressComponents) {
          ArrayList<String> types = component.getTypes();
          String firstType = types.get(0);
          if (firstType.equals(Constants.AA_LEVEL_1)) {
            clickedStateName = component.getLongName();
            if (mainActivity.getSelectedStateFragment().getFeature() == null) {
              mainActivity.onStateClicked(clickedStateName);
            } else {
              mapClearer.clearMap();
              if (clickedStateName.equals(mainActivity.getSelectedStateFragment()
                  .getFeature()
                  .getProperties()
                  .getPoliticalUnitName())) {
                resetStates();
              } else {
                mainActivity.onStateClicked(clickedStateName);
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
