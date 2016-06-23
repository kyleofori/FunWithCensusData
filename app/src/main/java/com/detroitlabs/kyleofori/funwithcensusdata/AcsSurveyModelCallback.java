package com.detroitlabs.kyleofori.funwithcensusdata;

import android.content.Context;
import com.detroitlabs.kyleofori.funwithcensusdata.interfaces.SurveyDataResponder;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcsSurveyModelCallback implements Callback<ArrayList<ArrayList<String>>> {
  private SurveyDataResponder surveyDataResponder;
  private String variable;

  public AcsSurveyModelCallback(Context context) {
    surveyDataResponder = (SurveyDataResponder) context;
  }

  public String getVariable() {
    return variable;
  }

  @Override public void onResponse(Call<ArrayList<ArrayList<String>>> call,
      Response<ArrayList<ArrayList<String>>> response) {
    ArrayList<ArrayList<String>> acsSurveyModelList = response.body();
    variable = "Number of 18- and 19-year-old black men: " + acsSurveyModelList.get(1).get(1);
    surveyDataResponder.onAccessedSurveyData(variable);
  }

  @Override public void onFailure(Call<ArrayList<ArrayList<String>>> call, Throwable t) {
    t.printStackTrace();
  }
}
