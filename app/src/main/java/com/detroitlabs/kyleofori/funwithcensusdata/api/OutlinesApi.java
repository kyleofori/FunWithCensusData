package com.detroitlabs.kyleofori.funwithcensusdata.api;

import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;

import retrofit2.Callback;
import retrofit2.http.GET;

public interface OutlinesApi {
    @GET("/wupl/Stuff/gz_2010_us_040_00_20m.json")
    void getOutlinesModel(Callback<OutlinesModel> callback);
    //callback is the response from the server which is now in the POJO
}
