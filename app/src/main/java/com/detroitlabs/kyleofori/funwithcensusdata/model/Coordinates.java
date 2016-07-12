package com.detroitlabs.kyleofori.funwithcensusdata.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Coordinates extends ArrayList<List<List<Object>>> implements Parcelable {

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
  }

  public Coordinates() {
  }

  protected Coordinates(Parcel in) {
  }

  public static final Creator<Coordinates> CREATOR = new Creator<Coordinates>() {
    @Override public Coordinates createFromParcel(Parcel source) {
      return new Coordinates(source);
    }

    @Override public Coordinates[] newArray(int size) {
      return new Coordinates[size];
    }
  };
}
