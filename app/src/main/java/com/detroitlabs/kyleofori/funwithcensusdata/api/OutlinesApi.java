package com.detroitlabs.kyleofori.funwithcensusdata.api;

import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by kyleofori on 7/1/15.
 */
public interface OutlinesApi {
    @GET("/wupl/Stuff/gz_2010_us_040_00_20m.json")
    //here is the other url part.best way is to start using /
    List<LatLng> getFeed(Callback<OutlinesModel> response);
    //string user is for passing values from edittext for eg: user=basil2style,google
    //response is the response from the server which is now in the POJO
}
