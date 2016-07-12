package com.detroitlabs.kyleofori.funwithcensusdata.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Feature implements Parcelable {
  private PropertySet properties;

  public PropertySet getProperties() {
    return properties;
  }

  private Geometry geometry;

  public Geometry getGeometry() {
    return geometry;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.properties, flags);
    dest.writeParcelable(this.geometry, flags);
  }

  public Feature() {
  }

  protected Feature(Parcel in) {
    this.properties = in.readParcelable(PropertySet.class.getClassLoader());
    this.geometry = in.readParcelable(Geometry.class.getClassLoader());
  }

  public static final Creator<Feature> CREATOR = new Creator<Feature>() {
    @Override public Feature createFromParcel(Parcel source) {
      return new Feature(source);
    }

    @Override public Feature[] newArray(int size) {
      return new Feature[size];
    }
  };
}
