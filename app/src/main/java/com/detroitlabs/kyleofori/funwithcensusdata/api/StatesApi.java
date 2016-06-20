package com.detroitlabs.kyleofori.funwithcensusdata.api;

import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;

import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StatesApi {

    @GET("/geocode/json")
    void getStatesModel(@Query("latlng") String latLngString, Callback<StatesModel> callback);
}
