package com.detroitlabs.kyleofori.funwithcensusdata.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class PropertySet implements Parcelable {
  @SerializedName("NAME") private String politicalUnitName;

  @SerializedName("STATE") private String stateNumber;

  public String getPoliticalUnitName() {
    return politicalUnitName;
  }

  public String getStateNumber() {
    return stateNumber;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.politicalUnitName);
    dest.writeString(this.stateNumber);
  }

  public PropertySet() {
  }

  protected PropertySet(Parcel in) {
    this.politicalUnitName = in.readString();
    this.stateNumber = in.readString();
  }

  public static final Creator<PropertySet> CREATOR = new Creator<PropertySet>() {
    @Override public PropertySet createFromParcel(Parcel source) {
      return new PropertySet(source);
    }

    @Override public PropertySet[] newArray(int size) {
      return new PropertySet[size];
    }
  };
}
