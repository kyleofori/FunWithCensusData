package com.detroitlabs.kyleofori.funwithcensusdata;

import android.util.Log;
import java.util.ArrayList;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AcsSurveyModelCallback implements Callback<ArrayList<ArrayList<String>>> {
  private String variable;

  public String getVariable() {
    return variable;
  }

  @Override public void success(ArrayList<ArrayList<String>> acsSurveyModelList, Response response) {
    Log.i("AcsSurveyModelCallback", "success: this method has been reached");
    variable = "Number of 18- and 19-year-old black men: " + acsSurveyModelList.get(1).get(1);
    Log.i("AcsSurveyModelCallback", variable);
  }

  @Override public void failure(RetrofitError error) {
    error.printStackTrace();
    Log.i("AcsSurveyModelCallback", "failure: method has not been reached");
  }
}
