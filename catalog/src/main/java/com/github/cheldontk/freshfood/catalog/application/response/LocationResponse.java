package com.github.cheldontk.freshfood.catalog.application.response;

public class LocationResponse {
    public Double longitude;
    public Double latitude;

    public LocationResponse(Double longitude, Double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
