package com.detroitlabs.kyleofori.funwithcensusdata;

import android.app.Fragment;
import android.os.Bundle;

import com.detroitlabs.kyleofori.funwithcensusdata.model.OutlinesModel;

public class SelectedStateFragment extends Fragment {
    private OutlinesModel.Feature feature;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setData(OutlinesModel.Feature feature) {
        this.feature = feature;
    }

    public OutlinesModel.Feature getData() {
        return feature;
    }
}
