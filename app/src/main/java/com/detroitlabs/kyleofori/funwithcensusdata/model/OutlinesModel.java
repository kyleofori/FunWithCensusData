package com.detroitlabs.kyleofori.funwithcensusdata.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by kyleofori on 7/1/15.
 */
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

            public String getPoliticalUnitName() {
                return politicalUnitName;
            }
        }

        private Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        public class Geometry {
            @SerializedName("coordinates")
            private double[][][] allLandmasses;

            public double[][][] getAllLandmasses() {
                return allLandmasses;
            }

        }
    }
}
