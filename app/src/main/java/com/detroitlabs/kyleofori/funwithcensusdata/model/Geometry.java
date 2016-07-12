package com.detroitlabs.kyleofori.funwithcensusdata.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Geometry implements Parcelable {
  private String type;

  public String getType() {
    return type;
  }

  private Coordinates coordinates;

  public Coordinates getCoordinates() {
    return coordinates;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.type);
    dest.writeParcelable(this.coordinates, flags);
  }

  public Geometry() {
  }

  protected Geometry(Parcel in) {
    this.type = in.readString();
    this.coordinates = in.readParcelable(Coordinates.class.getClassLoader());
  }

  public static final Creator<Geometry> CREATOR = new Creator<Geometry>() {
    @Override public Geometry createFromParcel(Parcel source) {
      return new Geometry(source);
    }

    @Override public Geometry[] newArray(int size) {
      return new Geometry[size];
    }
  };
}

