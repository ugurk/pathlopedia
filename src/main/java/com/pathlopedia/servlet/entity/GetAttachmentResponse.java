package com.pathlopedia.servlet.entity;

import com.pathlopedia.ds.entity.Attachment;

import java.util.Date;
import java.util.List;

public final class GetAttachmentResponse {
    public final String id;
    public final String text;
    public final int score;
    public final boolean scored;
    public final Date createdAt;
    public final List<CommentResponse> comments;
    public final Attachment.Type type;

    public GetAttachmentResponse(
            String id,
            String text,
            int score,
            boolean scored,
            Date createdAt,
            List<CommentResponse> comments,
            Attachment.Type type) {
        this.id = id;
        this.text = text;
        this.score = score;
        this.scored = scored;
        this.createdAt = createdAt;
        this.comments = comments;
        this.type = type;
    }
}
