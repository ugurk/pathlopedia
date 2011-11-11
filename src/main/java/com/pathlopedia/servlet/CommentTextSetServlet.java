package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Comment;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class CommentTextSetServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Validate given text.
        String text = getTrimmedParameter("text");
        Comment.validateText(text);

        // Fetch the comment.
        Comment comment = DatastorePortal.safeGet(Comment.class,
                getTrimmedParameter("comment"));

        // Check comment visibility.
        if (!comment.isVisible())
            throw new ServletException("Inactive comment!");

        // Check comment accessibility.
        if (!comment.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Update the comment.
        DatastorePortal.safeUpdate(comment,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Comment.class)
                        .set("text", text)
                        .set("updatedAt", new Date()));

        // Return success.
        return new JSONResponse(0);
    }
}
