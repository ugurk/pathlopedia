package com.pathlopedia.servlet;

import com.google.code.morphia.Key;
import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Attachment;
import com.pathlopedia.datastore.entity.Comment;
import com.pathlopedia.datastore.entity.Parent;
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
        requireLogin();

        // Locate the attachment.
        Attachment attachment = DatastorePortal.safeGet(
                Attachment.class, getTrimmedParameter("attachment"));

        // Check attachment visibility.
        if (!attachment.isVisible())
            throw new ServletException("Inactive attachment!");

        // Check attachment accessibility.
        if (!attachment.isAccessible(getSessionUser()))
            throw new ServletException("Access denied!");

        // Create a new comment and save it.
        Comment comment = new Comment(
                new Parent(attachment),
                getSessionUser(),
                getTrimmedParameter("text"));
        Key<Comment> commentKey = DatastorePortal.safeSave(comment);

        // Add created comment into the attachment.
        DatastorePortal.safeUpdate(attachment,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Attachment.class)
                        .add("comments", commentKey)
                        .set("updatedAt", new Date()));

        // Return success with the comment id.
        return new JSONResponse(0, new ObjectIdEntity(comment.getId()));
    }
}
