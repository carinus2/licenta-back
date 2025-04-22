package com.start.pawpal_finder.dto;

import lombok.Data;

@Data
public class LatLng {
    private final double lat;
    private final double lng;

    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
