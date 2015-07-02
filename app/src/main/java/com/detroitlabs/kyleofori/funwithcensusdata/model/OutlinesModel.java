package com.detroitlabs.kyleofori.funwithcensusdata.model;

import java.util.List;

/**
 * Created by kyleofori on 7/1/15.
 */
public class OutlinesModel {


    private FeatureList featureList;

    public FeatureList getFeatureList() {
        return featureList;
    }

    public class FeatureList {

        private List<Feature> features;

        public Feature findFeatureByName(String politicalUnitName) {
            for (Feature f : features) {
                if (f.getProperties().getPoliticalUnitName().equals(politicalUnitName)) {
                    return f;
                }
            }
            return null;
        }

        public class Feature {
            private PropertySet properties;

            public PropertySet getProperties() {
                return properties;
            }

            public class PropertySet {
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
                private CoordinatesL3 completeOutline;

                public CoordinatesL3 getCompleteOutline() {
                    return completeOutline;
                }

                public class CoordinatesL3 {
                    private List<CoordinatesL2> landmasses;

                    public List<CoordinatesL2> getLandmasses() {
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
}
