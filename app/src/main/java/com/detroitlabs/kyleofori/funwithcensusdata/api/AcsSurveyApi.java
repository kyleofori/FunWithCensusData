package com.detroitlabs.kyleofori.funwithcensusdata.api;

import java.util.ArrayList;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface AcsSurveyApi {
  @GET("/")
  void getAcsSurveyModel(@Query("get") String nameAndVariable, @Query("for") String stateNumber, Callback<ArrayList<ArrayList<String>>> callback);
}
