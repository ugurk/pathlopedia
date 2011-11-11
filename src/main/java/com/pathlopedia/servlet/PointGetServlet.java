package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.*;
import com.pathlopedia.servlet.entity.CommentEntity;
import com.pathlopedia.servlet.entity.PathReferenceEntity;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.PointEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PointGetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch the point object.
        Point point = DatastorePortal.safeGet(
                Point.class, getTrimmedParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Get session user.
        User user = getSessionUser();

        // Check point accessibility.
        if (!point.isAccessible(user))
            throw new ServletException("Access denied!");

        // Fetch comments.
        List<CommentEntity> comments =
                new ArrayList<CommentEntity>();
        for (Comment comment : point.getComments())
            if (comment.isVisible())
                comments.add(new CommentEntity(
                        comment.getId().toString(),
                        comment.getUser().getId().toString(),
                        comment.getUser().getName(),
                        comment.getText(),
                        comment.getScore(),
                        comment.isScorable(user),
                        comment.getUpdatedAt()));

        // Fetch attachments.
        List<PointEntity.AttachmentResponse> attachments =
                new ArrayList<PointEntity.AttachmentResponse>();
        for (Attachment attachment : point.getAttachments())
            if (attachment.isVisible())
                attachments.add(new PointEntity.AttachmentResponse(
                        attachment.getId().toString(),
                        attachment.getText(),
                        attachment.getType(),
                        attachment.getImage()));

        // Create a path reference. (For parent access.)
        Path path = point.getParent().getPath();
        PathReferenceEntity pathEntity = null;
        if (path != null)
             pathEntity = new PathReferenceEntity(
                     path.getId().toString(),
                     path.getTitle());

        // Pack and return the result.
        return new JSONResponse(0, new PointEntity(
                point.getId().toString(),
                point.getLocation(),
                point.getUser().getId().toString(),
                point.getUser().getName(),
                point.getTitle(),
                point.getText(),
                point.getScore(),
                point.isScorable(user),
                point.getUpdatedAt(),
                pathEntity,
                comments,
                attachments));
    }
}
