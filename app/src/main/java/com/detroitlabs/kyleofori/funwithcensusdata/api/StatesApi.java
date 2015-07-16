package com.detroitlabs.kyleofori.funwithcensusdata.api;

import com.detroitlabs.kyleofori.funwithcensusdata.model.StatesModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by kyleofori on 7/16/15.
 */
public interface StatesApi {

    @GET("/geocode/json")
    void getStatesModel(@Query("latlng") String latLngString, Callback<StatesModel> callback);
}
