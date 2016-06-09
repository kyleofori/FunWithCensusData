package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.Context;
import android.os.Bundle;
import com.detroitlabs.kyleofori.funwithcensusdata.api.OutlinesApi;
import com.detroitlabs.kyleofori.funwithcensusdata.dialogs.OutsideClickDialogFragment;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;
import com.detroitlabs.kyleofori.funwithcensusdata.utils.Constants;
import java.util.ArrayList;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OutlineCallMaker implements Callback<StatesModel> {

  public Callback<OutlinesModel> outlinesModelCallback;
  public String clickedStateName;
  private MapClearingInterface mapClearingInterface;
  private MainActivity mainActivity;

  public OutlineCallMaker(Context context) {
    outlinesModelCallback = (Callback<OutlinesModel>) context;
    mapClearingInterface = (MapClearingInterface) context;
    mainActivity = (MainActivity) context;
  }

  protected void makeHttpCallForStateOutlines() {
    RestAdapter restAdapter =
        new RestAdapter.Builder().setEndpoint(Constants.ERIC_CLST_API_BASE_URL).build();

    OutlinesApi outlinesApi = restAdapter.create(OutlinesApi.class);

    outlinesApi.getOutlinesModel(outlinesModelCallback);
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

  @Override public void success(StatesModel statesModel, Response response) {
    ArrayList<StatesModel.GoogleResult> results = statesModel.getResults();
    if (results.isEmpty()) {
      createOutsideClickDialogFragment(mainActivity.getString(R.string.body_of_water_message),
          "water");
    } else if (!statesModel.isInUSA()) {
      createOutsideClickDialogFragment(mainActivity.getString(R.string.other_land_message), "land");
    } else {
      ArrayList<StatesModel.GoogleResult.AddressComponent> addressComponents =
          results.get(0).getAddressComponents();
      for (StatesModel.GoogleResult.AddressComponent component : addressComponents) {
        ArrayList<String> types = component.getTypes();
        String firstType = types.get(0);
        if (firstType.equals(Constants.AA_LEVEL_1)) {
          clickedStateName = component.getLongName();
          if (mainActivity.getSelectedStateFragment().getFeature() == null) {
            makeHttpCallForStateOutlines();
          } else {
            mapClearingInterface.clearMap();
            if (clickedStateName.equals(mainActivity.getSelectedStateFragment()
                .getFeature()
                .getProperties()
                .getPoliticalUnitName())) {
              resetStates();
            } else {
              makeHttpCallForStateOutlines();
            }
          }
        }
      }
    }
  }

  @Override public void failure(RetrofitError error) {
    error.printStackTrace();
  }
}
