package com.pathlopedia.servlet.entity;

import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.Coordinate;
import com.pathlopedia.ds.entity.Image;

import java.util.Date;
import java.util.List;

public final class GetPointResponse {
    public final String id;
    public final Coordinate location;
    public final String userId;
    public final String userName;
    public final String title;
    public final String text;
    public final int score;
    public final boolean isScored;
    public final Date updatedAt;
    public final List<CommentResponse> comments;
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

    public GetPointResponse(
            String id,
            Coordinate location,
            String userId,
            String userName,
            String title,
            String text,
            int score,
            boolean scored,
            Date updatedAt,
            List<CommentResponse> comments,
            List<AttachmentResponse> attachments) {
        this.id = id;
        this.location = location;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.text = text;
        this.score = score;
        this.isScored = scored;
        this.updatedAt = updatedAt;
        this.comments = comments;
        this.attachments = attachments;
    }
}
