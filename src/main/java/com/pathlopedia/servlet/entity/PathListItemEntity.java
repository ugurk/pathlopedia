package com.pathlopedia.servlet.entity;

import com.pathlopedia.ds.entity.Coordinate;

import java.util.List;

public final class PathListItemEntity {
    public final String id;
    public final String userId;
    public final String userName;
    public final String title;
    public final int score;
    public final List<Coordinate> corners;
    public final List<PointListItemEntity> points;

    public PathListItemEntity(
            String id,
            String userId,
            String userName,
            String title,
            int score,
            List<Coordinate> corners,
            List<PointListItemEntity> points) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.score = score;
        this.corners = corners;
        this.points = points;
    }
}
