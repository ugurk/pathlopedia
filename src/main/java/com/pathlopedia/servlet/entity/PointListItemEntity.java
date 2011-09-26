package com.pathlopedia.servlet.entity;

import com.pathlopedia.ds.entity.Coordinate;

public final class PointListItemEntity {
    public final String id;
    public final Coordinate location;
    public final String title;

    public PointListItemEntity(String id, Coordinate location, String title) {
        this.id = id;
        this.location = location;
        this.title = title;
    }
}
