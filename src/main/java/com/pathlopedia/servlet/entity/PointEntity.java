package com.pathlopedia.servlet.entity;

import com.pathlopedia.datastore.entity.Attachment;
import com.pathlopedia.datastore.entity.Coordinate;
import com.pathlopedia.datastore.entity.Image;

import java.util.Date;
import java.util.List;

public final class PointEntity {
    public final String id;
    public final Coordinate location;
    public final String userId;
    public final String userName;
    public final String title;
    public final String text;
    public final int score;
    public final boolean scored;
    public final Date updatedAt;
    public final PathReferenceEntity path;
    public final List<CommentEntity> comments;
    public final List<AttachmentResponse> attachments;

    public static final class AttachmentResponse {
        public final String id;
        public final String text;
        public final Attachment.Type type;
        public final Image image;

        public AttachmentResponse(
                String id,
                String text,
                Attachment.Type type,
                Image image) {
            this.id = id;
            this.text = text;
            this.type = type;
            this.image = image;
        }
    }

    public PointEntity(
            String id,
            Coordinate location,
            String userId,
            String userName,
            String title,
            String text,
            int score,
            boolean scored,
            Date updatedAt,
            PathReferenceEntity path,
            List<CommentEntity> comments,
            List<AttachmentResponse> attachments) {
        this.id = id;
        this.location = location;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.text = text;
        this.score = score;
        this.scored = scored;
        this.updatedAt = updatedAt;
        this.path = path;
        this.comments = comments;
        this.attachments = attachments;
    }
}
