package com.pathlopedia.datastore.entity;

import com.google.code.morphia.annotations.Embedded;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

@Embedded
public class Coordinate {
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

    public static Coordinate parse(String inp) throws IOException {
        return (new ObjectMapper()).readValue(inp, Coordinate.class);
    }
}
