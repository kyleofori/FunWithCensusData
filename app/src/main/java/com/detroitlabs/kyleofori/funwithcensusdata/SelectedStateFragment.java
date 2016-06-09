package com.detroitlabs.kyleofori.funwithcensusdata;

import android.os.Bundle;
import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;

public class SelectedStateFragment extends android.support.v4.app.Fragment {
    private OutlinesModel.Feature feature;
    private String information;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setFeature(OutlinesModel.Feature feature) {
        this.feature = feature;
    }

    public OutlinesModel.Feature getFeature() {
        return feature;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
