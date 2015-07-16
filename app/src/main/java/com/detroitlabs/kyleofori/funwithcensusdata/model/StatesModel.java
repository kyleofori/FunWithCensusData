package com.detroitlabs.kyleofori.funwithcensusdata.model;

import java.util.ArrayList;

/**
 * Created by kyleofori on 7/16/15.
 */
public class StatesModel {
    private ArrayList<GoogleResult> results;

    public ArrayList<GoogleResult> getResults() {
        return results;
    }

    public class GoogleResult {
        private ArrayList<AddressComponent> address_components;

        public ArrayList<AddressComponent> getAddressComponents() {
            return address_components;
        }

        public class AddressComponent {
            private String long_name;

            private String short_name;

            private ArrayList<String> types;

            public String getLongName() {
                return long_name;
            }

            public String getShortName() {
                return short_name;
            }

            public ArrayList<String> getTypes() {
                return types;
            }
        }
    }
}
