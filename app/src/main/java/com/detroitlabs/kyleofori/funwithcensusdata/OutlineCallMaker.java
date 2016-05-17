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

    public Callback<OutlinesModel> callback;
    public String clickedState;
    private MapClearingInterface mapClearingInterface;
    private MainActivity mainActivity;

    public OutlineCallMaker(Context context) {
        callback = (Callback<OutlinesModel>) context;
        mapClearingInterface = (MapClearingInterface) context;
        mainActivity = (MainActivity) context;
    }

    @Override
    public void success(StatesModel statesModel, Response response) {
        ArrayList<StatesModel.GoogleResult> results = statesModel.getResults();
        if (results.isEmpty()) {
            createOutsideClickDialogFragment(mainActivity.getString(R.string.body_of_water_message), "water");
        } else if (!statesModel.isInUSA()) {
            createOutsideClickDialogFragment(mainActivity.getString(R.string.other_land_message), "land");
        } else {
            ArrayList<StatesModel.GoogleResult.AddressComponent> addressComponents = results.get(0).getAddressComponents();
            String stateName;
            for(StatesModel.GoogleResult.AddressComponent component: addressComponents) {
                ArrayList<String> types = component.getTypes();
                String firstType = types.get(0);
                if (firstType.equals(Constants.AA_LEVEL_1)) {
                    stateName = component.getLongName();
                    clickedState = stateName;
                    if (mainActivity.selectedState == null) {
                        makeHttpCallForStateOutlines();
                    } else {
                        mapClearingInterface.clearMap();
                        if (!clickedState.equals(mainActivity.selectedState)) {
                            makeHttpCallForStateOutlines(); //uses variable clickedState to retrieve outline
                        } else { //this is for if you just unselected the selected state
                            resetStates();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
    }

    protected void makeHttpCallForStateOutlines() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.ERIC_CLST_API_BASE_URL)
                .build();

        OutlinesApi outlinesApi = restAdapter.create(OutlinesApi.class);

        outlinesApi.getOutlinesModel(callback);
        mainActivity.selectedState = clickedState;
    }

    private void createOutsideClickDialogFragment(String message, String tag) {
        OutsideClickDialogFragment dialog = new OutsideClickDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putCharSequence("message", message);
        dialog.setArguments(arguments);
        dialog.show(mainActivity.getFragmentManager(), tag);
    }

    private void resetStates() {
        mainActivity.selectedState = null;
        clickedState = null;
    }
}
