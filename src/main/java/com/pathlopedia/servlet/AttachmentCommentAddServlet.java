package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.Comment;
import com.pathlopedia.ds.entity.Parent;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class AttachmentCommentAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);
        Datastore ds = DatastorePortal.getDatastore();

        // Locate the attachment.
        Attachment attachment = DatastorePortal.safeGet(
                Attachment.class, req.getParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            throw new ServletException("Inactive attachment!");

        // TODO Check attachment accessibility.
        
        // Create a new comment and save it.
        Comment comment = new Comment(
                new Parent(attachment),
                (User) req.getSession().getAttribute("user"),
                req.getParameter("text"));
        Key<Comment> commentKey = ds.save(comment);

        // Add created comment into the attachment.
        DatastorePortal.safeUpdate(attachment,
                ds.createUpdateOperations(Attachment.class)
                        .add("comments", commentKey)
                        .set("updatedAt", new Date()));

        // Return success with the comment id.
        return new JSONResponse(0, new ObjectIdEntity(comment.getId()));
    }
}
