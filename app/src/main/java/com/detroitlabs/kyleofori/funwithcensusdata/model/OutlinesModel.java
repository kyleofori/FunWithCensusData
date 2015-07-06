package com.detroitlabs.kyleofori.funwithcensusdata.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

/**
 * Created by kyleofori on 7/1/15.
 */
public class OutlinesModel {

    private Collection<Feature> features;

    public Collection<Feature> getFeatures() {
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
            private CoordinatesL3 completeOutline;

            public CoordinatesL3 getCompleteOutline() {
                return completeOutline;
            }

            public class CoordinatesL3 {
                private CoordinatesL2[] landmasses;

                public CoordinatesL2[] getLandmasses() {
                    return landmasses;
                }

                public class CoordinatesL2 {
                    private double[] coordinatePair;

                    public double[] getCoordinatePair() {
                        return coordinatePair;
                    }
                }
            }
        }
    }
}
