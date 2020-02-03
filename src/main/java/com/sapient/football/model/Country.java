package com.sapient.football.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Country {
    @JsonProperty("Country_id")
    private int Country_id;
    @JsonProperty("country_name")
    private String country_name;

    public int getCountry_id() {
        return Country_id;
    }

    public void setCountry_id(int country_id) {
        Country_id = country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
}
