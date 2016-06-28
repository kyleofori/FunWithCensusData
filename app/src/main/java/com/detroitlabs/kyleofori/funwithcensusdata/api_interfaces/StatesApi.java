package com.detroitlabs.kyleofori.funwithcensusdata.api_interfaces;

import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StatesApi {

  @GET("geocode/json") Call<StatesModel> getStatesModel(@Query("latlng") String latLngString);
}
