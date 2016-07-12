package com.detroitlabs.kyleofori.funwithcensusdata.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class OutlinesModel implements Parcelable {
  private ArrayList<Feature> features;

  public ArrayList<Feature> getFeatures() {
    return features;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(this.features);
  }

  public OutlinesModel() {
  }

  protected OutlinesModel(Parcel in) {
    this.features = in.createTypedArrayList(Feature.CREATOR);
  }

  public static final Creator<OutlinesModel> CREATOR = new Creator<OutlinesModel>() {
    @Override public OutlinesModel createFromParcel(Parcel source) {
      return new OutlinesModel(source);
    }

    @Override public OutlinesModel[] newArray(int size) {
      return new OutlinesModel[size];
    }
  };
}
