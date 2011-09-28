package com.pathlopedia.servlet.entity;

import com.pathlopedia.ds.entity.Coordinate;

import java.util.Date;
import java.util.List;

public final class PathEntity {
    public final String id;
    public final String userId;
    public final String userName;
    public final String title;
    public final String text;
    public final int score;
    public final boolean scored;
    public final Date updatedAt;
    public final List<PointListItemEntity> points;
    public final List<CommentEntity> comments;

    public PathEntity(
            String id,
            String userId,
            String userName,
            String title,
            String text,
            int score,
            boolean scored,
            Date updatedAt,
            List<PointListItemEntity> points,
            List<CommentEntity> comments) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.text = text;
        this.score = score;
        this.scored = scored;
        this.updatedAt = updatedAt;
        this.points = points;
        this.comments = comments;
    }
}
