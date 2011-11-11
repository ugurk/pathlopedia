package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.*;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class PathCommentAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Locate the path.
        Path path = DatastorePortal.safeGet(
                Path.class, getTrimmedParameter("path"));

        // Check path visibility.
        if (!path.isVisible())
            throw new ServletException("Inactive path!");

        // Check attachment accessibility.
        if (!path.isAccessible(getSessionUser()))
            throw new ServletException("Access denied!");

        // Create and save the comment.
        Comment comment = new Comment(
                new Parent(path),
                getSessionUser(),
                getTrimmedParameter("text"));
        DatastorePortal.safeSave(comment);

        // Update the path.
        DatastorePortal.safeUpdate(path,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Path.class)
                        .add("comments", comment)
                        .set("updatedAt", new Date()));

        // Return success with the created comment id.
        return new JSONResponse(0, new ObjectIdEntity(comment.getId()));
    }
}
