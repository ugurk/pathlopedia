package com.pathlopedia.servlet.entity;

import java.util.Date;

public final class CommentEntity {
    public final String id;
    public final String userId;
    public final String userName;
    public final String text;
    public final int score;
    public final boolean isScored;
    public final Date createdAt;

    public CommentEntity(
            String id,
            String userId,
            String userName,
            String text,
            int score,
            boolean scored,
            Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.score = score;
        this.isScored = scored;
        this.createdAt = createdAt;
    }
}
