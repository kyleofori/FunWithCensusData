package com.detroitlabs.kyleofori.funwithcensusdata.api;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AcsSurveyApi {
  @GET("acs1")
  Call<ArrayList<ArrayList<String>>> getAcsSurveyInformation(@Query("get") String nameAndVariable, @Query("for") String stateNumber);
}
