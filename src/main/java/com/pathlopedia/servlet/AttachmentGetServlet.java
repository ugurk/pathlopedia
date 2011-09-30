package com.pathlopedia.servlet;

import com.google.code.morphia.Key;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.Comment;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.entity.CommentEntity;
import com.pathlopedia.servlet.entity.AttachmentEntity;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class AttachmentGetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);

        // Fetch the attachment.
        Attachment attachment = DatastorePortal.safeGet(
                Attachment.class, req.getParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            throw new ServletException("Inactive attachment!");

        // Get user key.
        @SuppressWarnings("unchecked")
        Key<User> userKey = (Key<User>) req.getSession().getAttribute("userKey");

        // Fetch attachment comments and pack properly.
        List<CommentEntity> comments = new ArrayList<CommentEntity>();
        for (Comment comment : attachment.getComments())
            if (comment.isVisible())
                comments.add(new CommentEntity(
                        comment.getId().toString(),
                        comment.getUser().getId().toString(),
                        comment.getUser().getName(),
                        comment.getText(),
                        comment.getScore(),
                        comment.getScorers().contains(userKey),
                        comment.getUpdatedAt()));

        // Pack and return the result.
        return new JSONResponse(0, new AttachmentEntity(
                attachment.getId().toString(),
                attachment.getText(),
                attachment.getScore(),
                attachment.getScorers().contains(userKey),
                attachment.getUpdatedAt(),
                comments,
                attachment.getType()));
    }
}
