package com.pathlopedia.servlet;

import com.google.code.morphia.Key;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.Comment;
import com.pathlopedia.ds.entity.Point;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.entity.CommentResponse;
import com.pathlopedia.servlet.entity.JSONResponse;
import com.pathlopedia.servlet.entity.GetPointResponse;
import com.pathlopedia.servlet.entity.WritableResponse;
import com.pathlopedia.servlet.wrapper.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class GetPointServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the point object.
        // TODO: Access permission checks.
        Point point = DatastorePortal.safeGet(
                Point.class, req.getParameter("point"));

        // Get user key.
        @SuppressWarnings("unchecked")
        Key<User> userKey = (Key<User>) req.getSession().getAttribute("userKey");

        // Fetch comments.
        List<CommentResponse> comments =
                new ArrayList<CommentResponse>();
        for (Comment comment : point.getComments())
            comments.add(new CommentResponse(
                    comment.getId().toString(),
                    comment.getUser().getId().toString(),
                    comment.getUser().getName(),
                    comment.getText(),
                    comment.getScore(),
                    comment.getScorers().contains(userKey),
                    comment.getCreatedAt()));

        // Fetch attachments.
        List<GetPointResponse.AttachmentResponse> attachments =
                new ArrayList<GetPointResponse.AttachmentResponse>();
        for (Attachment attachment : point.getAttachments())
            attachments.add(new GetPointResponse.AttachmentResponse(
                    attachment.getId().toString(),
                    attachment.getText(),
                    attachment.getType(),
                    attachment.getImage()));

        // Pack and return the result.
        return new JSONResponse(0, new GetPointResponse(
                point.getId().toString(),
                point.getLocation(),
                point.getUser().getId().toString(),
                point.getUser().getName(),
                point.getTitle(),
                point.getText(),
                point.getScore(),
                point.getScorers().contains(userKey),
                point.getUpdatedAt(),
                comments,
                attachments));
    }
}
