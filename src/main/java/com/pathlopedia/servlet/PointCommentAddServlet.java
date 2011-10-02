package com.pathlopedia.servlet;

import com.google.code.morphia.Datastore;
import com.pathlopedia.ds.DatastorePortal;
import com.pathlopedia.ds.entity.Comment;
import com.pathlopedia.ds.entity.Parent;
import com.pathlopedia.ds.entity.Point;
import com.pathlopedia.ds.entity.User;
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
        requireLogin(req);
        Datastore ds = DatastorePortal.getDatastore();

        // Locate the point.
        Point point = DatastorePortal.safeGet(
                Point.class, req.getParameter("point"));

        // Check point visibility.
        if (!point.isVisible())
            throw new ServletException("Inactive point!");

        // TODO Check point accessibility.

        // Create and save the comment.
        Comment comment = new Comment(
                new Parent(point),
                (User) req.getSession().getAttribute("user"),
                req.getParameter("text"));
        DatastorePortal.safeSave(comment);

        // Update the point.
        DatastorePortal.safeUpdate(point,
                ds.createUpdateOperations(Point.class)
                        .add("comments", comment)
                        .set("updatedAt", new Date()));

        // Return success with the created comment id.
        return new JSONResponse(0, new ObjectIdEntity(comment.getId()));
    }
}
