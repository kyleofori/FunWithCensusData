package com.detroitlabs.kyleofori.funwithcensusdata.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OutlinesModel {
    private ArrayList<Feature> features;

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public class Feature {
        private PropertySet properties;

        public PropertySet getProperties() {
            return properties;
        }

        public class PropertySet {
            @SerializedName("NAME")
            private String politicalUnitName;

            @SerializedName("STATE")
            private String stateNumber;

            public String getPoliticalUnitName() {
                return politicalUnitName;
            }

            public String getStateNumber() {
                return stateNumber;
            }
        }

        private Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        public class Geometry {
            private String type;

            public String getType() {
                return type;
            }

            private Object coordinates;

            public Object getCoordinates() {
                return coordinates;
            }
        }
    }
}
