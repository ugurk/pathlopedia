package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Comment;
import com.pathlopedia.servlet.base.PostMethodServlet;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.response.WritableResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class CommentDelServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Fetch the comment.
        Comment comment = DatastorePortal.safeGet(Comment.class,
                getTrimmedParameter("comment"));

        // Check comment visibility.
        if (!comment.isVisible())
            throw new ServletException("Inactive comment!");

        // Validate that user is the comment owner.
        if (!comment.isEditable(getSessionUser()))
            throw new ServletException("Access denied!");

        // Deactivate the comment.
        comment.deactivate();

        // Return success.
        return new JSONResponse(0);
    }
}
