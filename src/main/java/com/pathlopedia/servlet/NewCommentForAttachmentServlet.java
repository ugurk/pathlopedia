package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.UpdateResults;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Attachment;
import com.pathlopedia.ds.entity.Comment;
import com.pathlopedia.ds.entity.Parent;
import com.pathlopedia.ds.entity.User;
import com.pathlopedia.servlet.entity.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdResponse;
import com.pathlopedia.servlet.entity.WritableResponse;
import com.pathlopedia.servlet.wrapper.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class NewCommentForAttachmentServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin(req);
        Datastore ds = DatastorePortal.getDatastore();

        // Locate the attachment.
        Attachment attachment = DatastorePortal.safeGet(
                Attachment.class, req.getParameter("attachment"));
        
        // Create a new comment and save it.
        Comment comment = new Comment(
                new Parent(attachment.getId(), Parent.Type.ATTACHMENT),
                (User) req.getSession().getAttribute("user"),
                req.getParameter("text"));
        Key<Comment> commentKey = ds.save(comment);

        // Add created comment into the attachment.
        UpdateResults<Attachment> res = ds.update(attachment,
                ds.createUpdateOperations(Attachment.class)
                        .add("comments", commentKey));
        if (res.getHadError())
            throw new ServletException(
                    "Couldn't update parent attachment ("+
                    attachment.getId()+"): "+res.getError());

        // Return success with the comment id.
        return new JSONResponse(0, new ObjectIdResponse(comment.getId()));
    }
}
