package com.pathlopedia.servlet;

import com.pathlopedia.datastore.DatastorePortal;
import com.pathlopedia.datastore.entity.Comment;
import com.pathlopedia.datastore.entity.Parent;
import com.pathlopedia.datastore.entity.Point;
import com.pathlopedia.servlet.response.JSONResponse;
import com.pathlopedia.servlet.entity.ObjectIdEntity;
import com.pathlopedia.servlet.response.WritableResponse;
import com.pathlopedia.servlet.base.PostMethodServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

public final class PointCommentAddServlet extends PostMethodServlet {
    protected WritableResponse process(HttpServletRequest req)
            throws IOException, ServletException {
        requireLogin();

        // Locate the point.
        Point point = DatastorePortal.safeGet(
                Point.class, getTrimmedParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // Check point accessibility.
        if (!point.isAccessible(getSessionUser()))
            throw new ServletException("Access denied!");

        // Create and save the comment.
        Comment comment = new Comment(
                new Parent(point),
                getSessionUser(),
                getTrimmedParameter("text"));
        DatastorePortal.safeSave(comment);

        // Update the point.
        DatastorePortal.safeUpdate(point,
                DatastorePortal.getDatastore()
                        .createUpdateOperations(Point.class)
                        .add("comments", comment)
                        .set("updatedAt", new Date()));

        // Return success with the created comment id.
        return new JSONResponse(0, new ObjectIdEntity(comment.getId()));
    }
}
