package com.pathlopedia.ds.entity;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public final class Coordinate {
    private double lat;
    private double lng;

    @SuppressWarnings("unused")
    private Coordinate() {}

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
