package com.pathlopedia.servlet;

import com.google.code.morphia.Key;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.Comment;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.entity.CommentResponse;
import com.pathlopedia.servlet.entity.GetAttachmentResponse;
import com.pathlopedia.servlet.entity.JSONResponse;
import com.pathlopedia.servlet.entity.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class GetAttachmentServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the attachment.
        // TODO: Access permission checks.
        Attachment attachment = DatastorePortal.safeGet(
                Attachment.class, req.getParameter("attachment"));

        // Get user key.
        @SuppressWarnings("unchecked")
        Key<User> userKey = (Key<User>) req.getSession().getAttribute("userKey");

        // Fetch attachment comments and pack properly.
        List<CommentResponse> comments = new ArrayList<CommentResponse>();
        for (Comment comment : attachment.getComments())
            comments.add(new CommentResponse(
                    comment.getId().toString(),
                    comment.getUser().getId().toString(),
                    comment.getUser().getName(),
                    comment.getText(),
                    comment.getScore(),
                    comment.getScorers().contains(userKey),
                    comment.getUpdatedAt()));

        // Pack and return the result.
        return new JSONResponse(0, new GetAttachmentResponse(
                attachment.getId().toString(),
                attachment.getText(),
                attachment.getScore(),
                attachment.getScorers().contains(userKey),
                attachment.getUpdatedAt(),
                comments,
                attachment.getType()));
    }
}
